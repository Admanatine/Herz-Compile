package net.ada.compositer;

import net.ada.manifest.HerzAsset;
import net.ada.manifest.HerzPackage;
import net.ada.manifest.HerzVersion;
import net.ada.manifest.MixinSource;
import net.ada.manifest.PackagePart;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
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
        // --- Permission manifest ---
        Set<String> knownPermissions = Set.of("network", "render", "input", "config", "assets");
        List<String> perms = packageConfig.permissions();
        if (!perms.isEmpty()) {
            logger.info("Package " + packageConfig.package_name() + " declares permissions: " + perms);
            for (String perm : perms) {
                if (!knownPermissions.contains(perm)) {
                    logger.warning("Unknown permission '" + perm + "' declared by " + packageConfig.package_name());
                }
            }
        }

        // --- Resource folders (existing) ---
        packageConfig.resourceFolders().forEach(path -> {
            try {
                FileUtils.copyDirectory(packagePath.resolve(path).toFile(),
                        Main.getResourcePath().resolve(path).toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // --- Asset pipeline ---
        for (HerzAsset asset : packageConfig.assets()) {
            Path assetSrc = packagePath.resolve(asset.source());
            Path assetDst = Main.getResourcePath().resolve(asset.target());
            try {
                if (!Files.exists(assetSrc)) {
                    logger.warning("Asset source not found in package: " + asset.source());
                    continue;
                }
                Files.createDirectories(assetDst.getParent());
                if (Files.isDirectory(assetSrc)) {
                    FileUtils.copyDirectory(assetSrc.toFile(), assetDst.toFile());
                } else {
                    FileUtils.copyFile(assetSrc.toFile(), assetDst.toFile());
                }
                logger.info("Applied " + asset.type() + " asset: " + asset.source() + " -> " + asset.target());
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to apply asset " + asset.source(), e);
            }
        }
        if (Main.getSubpackage().isEmpty()) {
            Main.setSubpackage(Main.getPlatform().subplatforms().keySet().stream().toList());
        }
        Main.getSubpackage().forEach(path -> {
            try {
                Path classpath = Main.getRootPath().resolve(Main.getPlatform().subplatforms().get(path)).resolve("build").resolve("classes").resolve("java").resolve("main");
                var part = attachJavaClasses(packagePath.resolve(path + ".jar"), classpath);
                for (String mixinRef : part.mixinsRef()) {
                    Transformer.runTransformation(Main.getGson(), classpath, classpath.resolve(mixinRef));
                    FileUtils.delete(classpath.resolve(mixinRef).toFile());
                }
            } catch (IOException | URISyntaxException e) {
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
