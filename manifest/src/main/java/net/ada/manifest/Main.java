package net.ada.manifest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws Exception {
        EaglerPackage eaglerPackage = new EaglerPackage(
                "0.0.0",
                "ada:example_package",
                "0.0.1",
                "net.ada.core.main.HerzCore",
                UUID.randomUUID().toString(),
                List.of("Example Author"),
                List.of("A mod for modders!!!"),
                "eagler/1_8_8",
                "u53",
                "lax1dude",
                List.of("herz"),
                Map.of("ada:core", "f237331c-60f8-4fe5-98c2-c14fbc4ed143")
        );

        MixinSource mixinSource = new MixinSource(
                true,
                "net.ada.mixins",
                "JAVA_17",
                List.of("MinecraftMixin"),
                Map.of("default", 1),
                ""
        );
        HerzPlatform herzPlatform = new HerzPlatform(
                "eagler/1_8_8",
                "u53",
                "lax1dude",
                List.of("desktop", "teavm-js", "teavm-wasm_gc")
        );
        PackagePart packagePart = new PackagePart("eagler/1_8_8/teavm-js", List.of("mixins.common.json"));
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (args.length == 0) {
            Files.write(FileSystems.getDefault().getPath("mixins.common.json"), gson.toJson(mixinSource).getBytes());
            Files.write(FileSystems.getDefault().getPath("eaglerPackage.json"), gson.toJson(eaglerPackage).getBytes());
            Files.write(FileSystems.getDefault().getPath("package-part.json"), gson.toJson(packagePart).getBytes());
            Files.write(FileSystems.getDefault().getPath("herz-platform.json"), gson.toJson(herzPlatform).getBytes());

        }
        else if (args.length == 1) {
            Files.write(Path.of(args[0]).resolve("mixins.common.json"), gson.toJson(mixinSource).getBytes());
            Files.write(Path.of(args[0]).resolve("herz-package.json"), gson.toJson(eaglerPackage).getBytes());
            Files.write(Path.of(args[0]).resolve("package-part.json"), gson.toJson(packagePart).getBytes());
            Files.write(Path.of(args[0]).resolve("herz-platform.json"), gson.toJson(herzPlatform).getBytes());
        }
        else {
            System.out.println("Usage: jar -jar <jar loc>\n or jar -jar <jar loc> <target generation folder>");
        }
    }
}
