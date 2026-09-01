package net.ada.manifest;

import java.util.List;
import java.util.Map;

public record HerzPackage(
        String herz_version,
        String package_name,
        String package_version,
        String uuid,
        List<String> authors,
        List<String> description,
        String platformTarget,
        String platformTargetVersion,
        String platformAuthor,
        List<String> resourceFolders,
        Map<String, String> dependencies,
        // Declared permissions — compositer enforces and logs these.
        // Known values: "network", "render", "input", "config", "assets"
        List<String> permissions,
        // Asset overrides applied during compose (texture/sound/lang/resource).
        List<HerzAsset> assets
){
    public List<String> permissions() {
        return permissions != null ? permissions : List.of();
    }

    public List<HerzAsset> assets() {
        return assets != null ? assets : List.of();
    }
}
