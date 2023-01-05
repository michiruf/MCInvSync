package mrnavastar.invsync.data.persistence;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mrnavastar.invsync.Logger;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.apache.logging.log4j.Level;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class NbtCompoundPersister extends AbstractStringPersister<NbtCompound> {

    private static final int WIDTH = 1000;
    private static final NbtCompoundPersister singleTon = new NbtCompoundPersister();

    @SuppressWarnings("unused")
    public static NbtCompoundPersister getSingleton() {
        return singleTon;
    }

    private NbtCompoundPersister() {
        super(new Class[]{NbtCompound.class});
    }

    @Override
    public int getDefaultWidth() {
        return WIDTH;
    }

    @Override
    protected String getStringFromInstance(NbtCompound data) {
        return data.toString();
    }

    @Override
    protected NbtCompound getInstanceFromString(String data) {
        try {
            return StringNbtReader.parse(data);
        } catch (CommandSyntaxException e) {
            Logger.logException(Level.ERROR, e);
        }

        return null;
    }
}
