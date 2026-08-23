package net.ada.manifest;

import java.util.List;
import java.util.Map;

public record HerzPlatform
        (String platform,
         String version,
        String author,
         Map<String, String> subplatforms
         ){
}
