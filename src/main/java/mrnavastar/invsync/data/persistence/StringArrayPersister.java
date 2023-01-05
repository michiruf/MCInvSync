package mrnavastar.invsync.data.persistence;

import com.google.gson.JsonElement;
import com.j256.ormlite.field.types.StringType;
import mrnavastar.invsync.data.PersistenceUtil;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class StringArrayPersister extends AbstractStringPersister<String[]> {

    private static final StringArrayPersister singleTon = new StringArrayPersister();

    @SuppressWarnings("unused")
    public static StringArrayPersister getSingleton() {
        return singleTon;
    }

    private StringArrayPersister() {
        super(new Class[]{String[].class});
    }

    @Override
    protected String getStringFromInstance(String[] data) {
        return PersistenceUtil.SERIALIZER.toJson(data);
    }

    @Override
    protected String[] getInstanceFromString(String data) {
        return PersistenceUtil.SERIALIZER.fromJson(data, String[].class);
    }
}
