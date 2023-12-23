//package de.michiruf.invsync.base;
//
//import net.fabricmc.loader.impl.game.GameProvider;
//import net.fabricmc.loader.impl.launch.FabricLauncher;
//import net.fabricmc.loader.impl.util.log.Log;
//import net.fabricmc.loader.impl.util.log.LogCategory;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.ServiceLoader;
//import java.util.stream.Collectors;
//
//public class FabricTestLauncherExt {
//
//    public static GameProvider getGameProvider(FabricLauncher launcher, String[] args) {
//        List<GameProvider> failedProviders = new ArrayList<>();
//
//        for (GameProvider provider : ServiceLoader.load(GameProvider.class)) {
//            if (!provider.isEnabled())
//                continue; // don't attempt disabled providers and don't include them in the error report
//
//            if (provider.locateGame(launcher, args)) {
//                return provider;
//            }
//
//            failedProviders.add(provider);
//        }
//
//        String msg;
//        if (failedProviders.isEmpty())
//            msg = "No game providers present on the class path!";
//        else if (failedProviders.size() == 1)
//            msg = String.format("%s game provider couldn't locate the game! "
//                            + "The game may be absent from the class path, lacks some expected files, suffers from jar "
//                            + "corruption or is of an unsupported variety/version.",
//                    failedProviders.get(0).getGameName());
//        else
//            msg = String.format("None of the game providers (%s) were able to locate their game!",
//                    failedProviders.stream().map(GameProvider::getGameName).collect(Collectors.joining(", ")));
//        Log.error(LogCategory.GAME_PROVIDER, msg);
//
//        throw new RuntimeException(msg);
//    }
//}
