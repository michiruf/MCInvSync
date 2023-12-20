package de.michiruf.invsync.data.persistence;

import com.mojang.brigadier.StringReader;
import de.michiruf.invsync.Logger;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import org.apache.logging.log4j.Level;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class NbtListPersister extends AbstractStringPersister<NbtList> {

    public NbtListPersister() {
        super(new Class[]{NbtList.class}, true);
    }

    @Override
    protected String getStringFromInstance(NbtList data) {
        return data.toString();
    }

    @Override
    protected NbtList getInstanceFromString(String data) {
        try {
            var reader = new StringNbtReader(new StringReader(data));
            var readData = reader.parseElement();
            if (!(readData instanceof NbtList nbtList))
                throw new IllegalArgumentException("Data is not of type NbtList, where it should be");
            return nbtList;
        } catch (Exception e) {
            Logger.logException(Level.ERROR, e);
        }

        return null;
    }
}
