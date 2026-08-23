package net.ada.compositer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import net.ada.manifest.HerzPlatform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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

    public static void main(String[] args) throws IOException {
        gson = new GsonBuilder().setPrettyPrinting().create();
        String platformJSON = args[0];
        String rootProjectAddress = args[1];
        String packagesDir = args[2];
        resourcePath = Path.of(args[3]);

        platform = gson.fromJson(Files.readString(Path.of(platformJSON)), HerzPlatform.class);
        rootPath = Path.of(rootProjectAddress);
        packagesPath = rootPath.resolve(packagesDir);
        Path tempPackagePath = rootPath.resolve("build").resolve("tmp").resolve("packages");

        // extract phase
        FileUtils.deleteDirectory(tempPackagePath.toFile());
        Files.createDirectories(tempPackagePath);
        List<String> packageFolders = new ArrayList<>();
        try {
            Files.walk(packagesPath, 1).filter((y) -> {
                return filterExtension(y, ".hpckg");
            }).forEach(x -> {
                        try {
                            String packageName = x.getFileName().toString().split(".hpckg")[0];
                            ZipHandler.unzip(x, tempPackagePath.resolve(packageName));
                            packageFolders.add(packageName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // parse and push phase
        packageFolders.forEach(x -> {
                try {
                    new PackageHandler(tempPackagePath.resolve(x));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
         });
    }


    public static boolean filterExtension(Path path, String extension) {
        return path.getFileName().toString().endsWith(extension);
    }
}
