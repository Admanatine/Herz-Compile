package net.ada.compositer;

import net.ada.manifest.HerzPackage;
import net.ada.manifest.HerzVersion;
import net.ada.manifest.PackagePart;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackageHandler {
    Path packagePath;

    public PackageHandler(Path packagePath) throws IOException {
        this.packagePath = packagePath;
        begin();
    }

    public void begin() throws IOException {
        Logger logger = Main.getLogger();
        HerzPackage packageConfig = Main.getGson().fromJson(Files.readString(packagePath.resolve("herz-package.json")), HerzPackage.class);
        logger.info("Setting up package: " + packageConfig.package_name());
        if (!Objects.equals(packageConfig.platformTarget(), Main.getPlatform().platform())) {
            logger.log(Level.SEVERE, packageConfig.package_name() + " package on wrong target version. Skipping");
            return;
        }
        if (!Objects.equals(packageConfig.platformAuthor(), Main.getPlatform().author())) {
            logger.warning("Authors not the same. This CAN cause issues, as different platforms written by different people sometimes have differing functions. ");
        }
        if (!Objects.equals(packageConfig.platformTargetVersion(), Main.getPlatform().version())) {
            logger.warning("Package using unknown/outdated version of platform. May cause issues!");
        }
        if (!Objects.equals(packageConfig.herz_version(), HerzVersion.herz_version)) {
            logger.warning("Package " + packageConfig.package_name() + " using outdated Herz version " + packageConfig.herz_version());
        }
        packageConfig.resourceFolders().forEach(path -> {
            try {
                FileUtils.copyDirectory(packagePath.resolve(path).toFile(),
                        Main.getResourcePath().resolve(path).toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Main.getPlatform().subplatforms().keySet().forEach(path -> {
            try {
                var part = attachJavaClasses(packagePath.resolve(path + ".jar"), Main.getRootPath().resolve(Main.getPlatform().subplatforms().get(path)).resolve("build").resolve("classes").resolve("java").resolve("main"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public PackagePart attachJavaClasses(Path jar, Path target) throws IOException {
        ZipHandler.unzip(jar, target);
        Files.delete(target.resolve("META-INF/MANIFEST.MF"));
        Files.delete(target.resolve("META-INF"));
        PackagePart packagePart = Main.getGson().fromJson(Files.readString(target.resolve("package-part.json")), PackagePart.class);
        Files.delete(target.resolve("package-part.json"));
        return packagePart;
    }
}
