package de.michiruf.invsync.base;

import com.google.auto.service.AutoService;
import net.fabricmc.loader.impl.game.GameProvider;
import net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider;
import net.fabricmc.loader.impl.util.SystemProperties;
import org.junit.jupiter.api.Assertions;

import java.nio.file.Path;
import java.util.ServiceLoader;

@AutoService(GameProvider.class)
public class TestGameProvider extends MinecraftGameProvider implements GameProvider {

    public static void register() {
        System.setProperty(SystemProperties.SKIP_MC_PROVIDER, "true");
        System.setProperty("fabric.loader.useCompatibilityClassLoader", "true");

        // TODO Separate test for this
        var gameProviders = ServiceLoader.load(GameProvider.class).stream().toList();
        Assertions.assertTrue(gameProviders.size() > 1);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // TODO This should be one possibility to manipulate the directory, but it should be possible to set an argument as well
    @Override
    public Path getLaunchDirectory() {
        return Path.of("../src/test/resources/server1");
        //return super.getLaunchDirectory();
    }
}
