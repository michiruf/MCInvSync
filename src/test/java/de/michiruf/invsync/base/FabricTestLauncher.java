//package base;
//
//import net.fabricmc.api.EnvType;
//import net.fabricmc.loader.impl.launch.FabricLauncherBase;
//import net.fabricmc.loader.impl.launch.knot.KnotClassLoaderInterface;
//import net.fabricmc.loader.impl.util.log.Log;
//import net.fabricmc.loader.impl.util.log.LogCategory;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.jar.Manifest;
//
//public class FabricTestLauncher extends FabricLauncherBase {
//
//    private boolean isDevelopment = true;
//    private final List<Path> classPath = new ArrayList<>();
//
//    @Override
//    public String getTargetNamespace() {
//        // TODO: Won't work outside of Yarn
//        return isDevelopment ? "named" : "intermediary";
//    }
//
//    @Override
//    public List<Path> getClassPath() {
//        return classPath;
//    }
//
//    @Override
//    public void addToClassPath(Path path, String... allowedPrefixes) {
//        Log.debug(LogCategory.KNOT, "Adding " + path + " to classpath.");
//
//        classLoader.setAllowedPrefixes(path, allowedPrefixes);
//        classLoader.addCodeSource(path);
//    }
//
//    @Override
//    public void setAllowedPrefixes(Path path, String... prefixes) {
//        classLoader.setAllowedPrefixes(path, prefixes);
//    }
//
//    @Override
//    public void setValidParentClassPath(Collection<Path> paths) {
//        classLoader.setValidParentClassPath(paths);
//    }
//
//    @Override
//    public EnvType getEnvironmentType() {
//        return envType;
//    }
//
//    @Override
//    public boolean isClassLoaded(String name) {
//        return classLoader.isClassLoaded(name);
//    }
//
//    @Override
//    public Class<?> loadIntoTarget(String name) throws ClassNotFoundException {
//        return classLoader.loadIntoTarget(name);
//    }
//
//    @Override
//    public InputStream getResourceAsStream(String name) {
//        return classLoader.getClassLoader().getResourceAsStream(name);
//    }
//
//    @Override
//    public ClassLoader getTargetClassLoader() {
//        KnotClassLoaderInterface classLoader = this.classLoader;
//
//        return classLoader != null ? classLoader.getClassLoader() : null;
//    }
//
//    @Override
//    public byte[] getClassByteArray(String name, boolean runTransformers) throws IOException {
//        if (!unlocked) throw new IllegalStateException("early getClassByteArray access");
//
//        if (runTransformers) {
//            return classLoader.getPreMixinClassBytes(name);
//        } else {
//            return classLoader.getRawClassBytes(name);
//        }
//    }
//
//    @Override
//    public Manifest getManifest(Path originPath) {
//        return classLoader.getManifest(originPath);
//    }
//
//    @Override
//    public boolean isDevelopment() {
//        return isDevelopment;
//    }
//
//    @Override
//    public String getEntrypoint() {
//        return provider.getEntrypoint();
//    }
//}
