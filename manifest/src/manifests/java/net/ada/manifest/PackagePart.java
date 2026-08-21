package net.ada.manifest;

import java.util.List;

public record PackagePart(
        String targetPlatform,
        List<String> mixinsRef)
{
}
