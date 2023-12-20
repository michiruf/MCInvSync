package de.michiruf.invsync.data.persistence;

import com.google.gson.JsonElement;
import de.michiruf.invsync.data.PersistenceUtil;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class JsonElementPersister extends AbstractStringPersister<JsonElement> {

    public JsonElementPersister() {
        super(new Class[]{JsonElement.class}, true);
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
