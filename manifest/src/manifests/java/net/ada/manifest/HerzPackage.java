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
        Map<String, String> dependencies
){

}
