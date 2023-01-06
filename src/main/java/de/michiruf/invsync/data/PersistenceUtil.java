package de.michiruf.invsync.data;

import com.google.gson.Gson;
import com.j256.ormlite.field.DataPersisterManager;
import de.michiruf.invsync.data.persistence.JsonElementPersister;
import de.michiruf.invsync.data.persistence.NbtCompoundPersister;
import de.michiruf.invsync.data.persistence.NbtListPersister;
import de.michiruf.invsync.data.persistence.StringArrayPersister;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class PersistenceUtil {

    public static final Gson SERIALIZER = new Gson();

    public static void registerCustomPersisters() {
        DataPersisterManager.registerDataPersisters(
                JsonElementPersister.getSingleton(),
                NbtCompoundPersister.getSingleton(),
                NbtListPersister.getSingleton(),
                StringArrayPersister.getSingleton());
    }
}
