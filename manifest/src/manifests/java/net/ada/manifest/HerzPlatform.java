package net.ada.manifest;

import java.util.List;

public record HerzPlatform
        (String platform,
         String version,
        String author,
         List<String> subplatforms
         ){
}
