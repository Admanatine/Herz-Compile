package net.ada.manifest;

/**
 * Describes a single asset override bundled in a .hpckg.
 *
 * type   — one of: "texture", "sound", "lang", "resource"
 * source — path within the package zip (relative to the package root)
 * target — destination path inside the game resource tree
 */
public record HerzAsset(
        String type,
        String source,
        String target
) {
    public static final String TYPE_TEXTURE  = "texture";
    public static final String TYPE_SOUND    = "sound";
    public static final String TYPE_LANG     = "lang";
    public static final String TYPE_RESOURCE = "resource";
}
