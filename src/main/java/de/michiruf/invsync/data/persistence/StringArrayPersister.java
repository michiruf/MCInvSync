package de.michiruf.invsync.data.persistence;

import de.michiruf.invsync.data.PersistenceUtil;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class StringArrayPersister extends AbstractStringPersister<String[]> {

    public StringArrayPersister() {
        super(new Class[]{String[].class}, false);
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
