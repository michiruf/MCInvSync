package de.michiruf.invsync.data.custom_schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Michael Ruf
 * @since 2023-01-09
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DatabaseTypeSpecificDatabaseField {

    DatabaseTypeSpecificOverload[] value();
}
