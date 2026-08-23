package net.ada.compositer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHandler {
    /* Method taken from GPT */
    public static void unzip(Path zipFile, Path outputDir) throws IOException {
        Files.createDirectories(outputDir);

        try (InputStream input = Files.newInputStream(zipFile);
             ZipInputStream zip = new ZipInputStream(input)) {

            ZipEntry entry;

            while ((entry = zip.getNextEntry()) != null) {
                Path destination = outputDir
                        .resolve(entry.getName())
                        .normalize();

                // Prevent malicious ZIP entries like ../../something
                if (!destination.startsWith(outputDir.normalize())) {
                    throw new IOException(
                            "Invalid ZIP entry: " + entry.getName()
                    );
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(destination);
                } else {
                    Path parent = destination.getParent();

                    if (parent != null) {
                        Files.createDirectories(parent);
                    }

                    Files.copy(
                            zip,
                            destination,
                            StandardCopyOption.REPLACE_EXISTING
                    );
                }

                zip.closeEntry();
            }
        }
    }
}
