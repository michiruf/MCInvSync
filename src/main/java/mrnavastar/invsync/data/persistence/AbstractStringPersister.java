package mrnavastar.invsync.data.persistence;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.StringType;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public abstract class AbstractStringPersister<T> extends StringType {

    protected AbstractStringPersister(Class<?>[] classes) {
        super(SqlType.STRING, classes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object javaToSqlArg(FieldType fieldType, Object javaObject) {
        var data = (T) javaObject;
        return data != null ? getStringFromInstance(data) : null;
    }

    @Override
    public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) {
        return sqlArg != null ? getInstanceFromString((String) sqlArg) : null;
    }

    protected abstract String getStringFromInstance(T data);

    protected abstract T getInstanceFromString(String data);
}
