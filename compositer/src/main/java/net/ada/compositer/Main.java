package net.ada.compositer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.Setter;
import net.ada.manifest.HerzPackage;
import net.ada.manifest.HerzPlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
public class Main {
    @Getter
    static HerzPlatform platform;
    @Getter
    static Path rootPath, packagesPath, resourcePath;
    @Getter
    static Gson gson;
    @Getter
    static Logger logger = Logger.getLogger("[Herz Compositer]");
    @Getter @Setter
    static List<String> subpackage;
    @Getter
    static Map<String, String> dependencies = new HashMap<>();
    @Getter
    static Map<String, String> packages = new HashMap<>();
    public static void main(String[] args) throws IOException {
        gson = new GsonBuilder().setPrettyPrinting().create();
        String platformJSON = args[0];
        String rootProjectAddress = args[1];
        String packagesDir = args[2];
        resourcePath = Path.of(args[3]);
        subpackage = new ArrayList<>();
        if (args.length > 4) {
            for (int i = 4; i < args.length; i++) {
                subpackage.add(args[i]);
            }
        }

        platform = gson.fromJson(Files.readString(Path.of(platformJSON)), HerzPlatform.class);
        rootPath = Path.of(rootProjectAddress);
        packagesPath = rootPath.resolve(packagesDir);
        Path tempPackagePath = rootPath.resolve("build").resolve("tmp").resolve("packages");

        // extract phase
        FileUtils.deleteDirectory(tempPackagePath.toFile());
        Files.createDirectories(tempPackagePath);
        List<String> packageFolders = new ArrayList<>();
        try {
            List<Path> packages = new ArrayList<>();
            Files.walk(packagesPath, 1).filter((y) -> {
                return filterExtension(y, ".hpckg");
            }).forEach(packages::add
            );
            packages.forEach(x -> {
            try {
                    String packageName = x.getFileName().toString().split(".hpckg")[0];
                    ZipHandler.unzip(x, tempPackagePath.resolve(packageName));
                    packageFolders.add(packageName);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }}
            );


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // parse and push phase
        packageFolders.forEach(x -> {
                gson = new GsonBuilder().setPrettyPrinting().create();
            try {
                var h = gson.fromJson(Files.readString(tempPackagePath.resolve(x).resolve("herz-package.json")), HerzPackage.class);
                packages.put(h.uuid(), h.package_version());
                if (!h.dependencies().isEmpty()) {
                    h.dependencies().forEach((a, p) ->  {
                        String name = p.split(":")[0];
                        String vers = p.split(":")[1];
                                Main.getDependencies().put(name, vers);
                            }
                            );
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

         });
        AtomicBoolean passDependencyCheck = new AtomicBoolean(true);
        dependencies.keySet().forEach(x -> {
            if (!packages.containsKey(x)) {
                passDependencyCheck.set(false);
            }
            else {
                if(Objects.equals(dependencies.get(x), packages.get(x))) {
                    System.out.println("Package " + x + " is not the version required. " + dependencies.get(x) + " vs " + packages.get(x) );
                }
            }

        });
        if (passDependencyCheck.get()) {
            packageFolders.forEach(x -> {
                try {
                    new PackageHandler(tempPackagePath.resolve(x));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }
        else {
            System.out.println("Missing dependencies");
        }
    }


    public static boolean filterExtension(Path path, String extension) {
        return path.getFileName().toString().endsWith(extension);
    }
}
