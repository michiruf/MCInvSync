package mrnavastar.invsync.data.persistence;

import com.google.gson.JsonElement;
import mrnavastar.invsync.data.PersistenceUtil;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class JsonElementPersister extends AbstractStringPersister<JsonElement> {

    private static final int WIDTH = 1000;
    private static final JsonElementPersister singleTon = new JsonElementPersister();

    @SuppressWarnings("unused")
    public static JsonElementPersister getSingleton() {
        return singleTon;
    }

    private JsonElementPersister() {
        super(new Class[]{JsonElement.class});
    }

    @Override
    public int getDefaultWidth() {
        return WIDTH;
    }

    @Override
    protected String getStringFromInstance(JsonElement data) {
        return data.toString();
    }

    @Override
    protected JsonElement getInstanceFromString(String data) {
        return PersistenceUtil.SERIALIZER.fromJson(data, JsonElement.class);
    }
}
