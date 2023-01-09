package de.michiruf.invsync.data.custom_schema;

import com.j256.ormlite.db.DatabaseType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.DatabaseFieldConfig;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Ruf
 * @see <a href="https://github.com/j256/ormlite-core/issues/279">Issue on ORMLite github</a>
 * @since 2023-01-09
 */
public class OverloadableDatabaseTableConfig {

    /**
     * @see DatabaseTableConfig#fromClass(DatabaseType, Class)
     */
    public static <T> DatabaseTableConfig<T> fromClass(DatabaseType databaseType, Class<T> clazz) throws SQLException {
        var tableName = DatabaseTableConfig.extractTableName(databaseType, clazz);
        return new DatabaseTableConfig<>(databaseType, clazz, collectDatabaseFieldConfigs(databaseType, clazz, tableName));
    }

    /**
     * @see DatabaseTableConfig#extractFieldTypes(DatabaseType, Class, String)
     * @see DatabaseFieldConfig#fromDatabaseField(DatabaseType, String, Field, DatabaseField)
     */
    private static <T> List<DatabaseFieldConfig> collectDatabaseFieldConfigs(DatabaseType databaseType, Class<T> clazz, String tableName) throws SQLException {
        var fieldConfigs = new ArrayList<DatabaseFieldConfig>();

        for (Class<?> classWalk = clazz; classWalk != null; classWalk = classWalk.getSuperclass()) {
            Field[] fields = classWalk.getDeclaredFields();
            for (Field field : fields) {
                var specificFieldConfig = getDatabaseFieldConfigFromSpecificDatabaseFieldAnnotation(databaseType, field, tableName);
                if (specificFieldConfig != null) {
                    fieldConfigs.add(specificFieldConfig);
                    continue;
                }

                var fieldConfig = DatabaseFieldConfig.fromField(databaseType, tableName, field);
                if (fieldConfig != null) {
                    fieldConfigs.add(fieldConfig);
                }
            }
        }

        if (fieldConfigs.isEmpty()) {
            throw new IllegalArgumentException("No fields have a " + DatabaseField.class.getSimpleName() + " annotation in " + clazz);
        }

        return fieldConfigs;
    }

    private static DatabaseFieldConfig getDatabaseFieldConfigFromSpecificDatabaseFieldAnnotation(DatabaseType databaseType, Field field, String tableName) {
        var databaseSpecificField = field.getAnnotation(DatabaseTypeSpecificDatabaseField.class);
        if (databaseSpecificField != null) {
            for (DatabaseTypeSpecificOverload databaseTypeSpecificOverload : databaseSpecificField.value()) {
                if (databaseTypeSpecificOverload.typeName().equals(databaseType.getDatabaseName())) {
                    return DatabaseFieldConfig.fromDatabaseField(databaseType, tableName, field, databaseTypeSpecificOverload.databaseField());
                }
            }
        }

        return null;
    }
}
