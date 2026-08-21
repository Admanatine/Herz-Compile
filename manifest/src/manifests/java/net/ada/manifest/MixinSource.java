package net.ada.manifest;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

public record MixinSource
        (
         boolean required,
         @SerializedName(value = "package", alternate = "mixinPackage") String mixinPackage,
         String compatibilityLevel,
         List<String> mixins,
         Map<String, Integer> injectors, // target injects
         String refmap // later feature, dummy for now
        ){
}