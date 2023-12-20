package de.michiruf.invsync.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import de.michiruf.invsync.Logger;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Michael Ruf
 * @since 2023-12-20
 */
public class ConfigWrapper {

    private static final String configFileName = "invsync.json";
    private static final File configFile;
    private static final Gson mapper;

    static {
        configFile = FabricLoader.getInstance().getConfigDir().resolve(configFileName).toFile();
        mapper = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public Config config;

    public static ConfigWrapper load() throws IOException {
        var wrapper = new ConfigWrapper();

        if (!configFile.exists()) {
            Logger.log(Level.WARN, "Config not found, creating new one");
            // Additional save before loading, to have the file initially created
            wrapper.config = new Config();
            wrapper.save();
        }

        wrapper.config = mapper.fromJson(new JsonReader(new FileReader(configFile)), Config.class);
        return wrapper;
    }

    public void save() {
        try {
            var writer = new FileWriter(configFile);
            mapper.toJson(config, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Logger.log(Level.WARN, "Could not save json config file");
            Logger.logException(Level.WARN, e);
            throw new RuntimeException(e);
        }
    }
}
