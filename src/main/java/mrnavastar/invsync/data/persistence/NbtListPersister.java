package mrnavastar.invsync.data.persistence;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.field.types.DateType;
import com.j256.ormlite.field.types.StringType;
import com.j256.ormlite.support.DatabaseResults;
import com.mojang.brigadier.StringReader;
import mrnavastar.invsync.Logger;
import mrnavastar.sqlib.api.DataContainer;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.StringNbtReader;
import org.apache.logging.log4j.Level;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author Michael Ruf
 * @since 2023-01-05
 */
public class NbtListPersister extends AbstractStringPersister<NbtList> {

    private static final int WIDTH = 1000;
    private static final NbtListPersister singleTon = new NbtListPersister();

    @SuppressWarnings("unused")
    public static NbtListPersister getSingleton() {
        return singleTon;
    }

    private NbtListPersister() {
        super(new Class[]{NbtList.class});
    }

    @Override
    public int getDefaultWidth() {
        return WIDTH;
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
