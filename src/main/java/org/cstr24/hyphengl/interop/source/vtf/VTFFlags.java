package org.cstr24.hyphengl.interop.source.vtf;

public class VTFFlags {
    /**
     * Low quality, "pixel art" texture filtering.
     */
    public static final int PointSampling = 0x0001;

    /**
     * Medium quality texture filtering.
     */
    public static final int TrilinearSampling = 0x0002;

    /**
     * Clamp S coordinates.
     */
    public static final int FlagClampS = 0x0004;

    /**
     * Clamp T coordinates.
     */
    public static final int FlagClampT = 0x0008;

    /**
     * High quality texture filtering.
     */
    public static final int FlagAnisotropicFiltering = 0x0010;

    /**
     * Used in skyboxes. Makes sure edges are seamless.
     */
    public static final int FlagHintDXT5 = 0x0020;


    /**
     * Uses space RGB. Useful for High Gamuts. Deprecated in 7.5.
     */
    public static final int FlagSRGB = 0x0040;

    /**
     * Texture is a normal map.
     */
    public static final int FlagNormalMap = 0x0080;

    /**
     * Render largest mipmap only. (Does not delete existing mipmaps, just disables them.)
     */
    public static final int FlagNoMipmaps = 0x0100;

    /**
     * Not affected by texture resolution settings.
     */
    public static final int FlagNoLevelOfDetail = 0x0200;

    /**
     * If set, load mipmaps below 32x32 pixels.
     */
    public static final int FlagNoMinimumMipmap = 0x0400;

    /**
     * Texture is an procedural texture (code can modify it).
     */
    public static final int FlagProcedural = 0x0800;

    /**
     * One bit alpha channel used.
     */
    public static final int FlagOneBitAlpha = 0x1000;

    /**
     * Eight bit alpha channel used.
     */
    public static final int FlagEightBitAlpha = 0x2000;

    /**
     * Texture is an environment map.
     */
    public static final int FlagEnvironmentMap = 0x4000;

    /**
     * Texture is a render target.
     */
    public static final int FlagRenderTarget = 0x8000;

    /**
     * Texture is a depth render target.
     */
    public static final int FlagDepthRenderTarget = 0x10000;

    public static final int FlagNoDebugOverride = 0x20000;
    public static final int FlagSingleCopy = 0x40000;

    /**
     * SRGB correction has already been applied
     */
    public static final int FlagPreSRGB = 0x80000;

    /**
     * Fill the alpha channel with 1/Mipmap Level. Deprecated (Internal to VTEX?)
     */
    public static final int FlagOneOverMipmapLevelInAlpha = 0x80000;

    /**
     * (Internal to VTEX?)
     */
    public static final int FlagPreMultiplyColorByOneOverMipmapLevel = 0x100000;

    /**
     * Texture is a DuDv map. (Internal to VTEX?)
     */
    public static final int FlagNormalToDuDv = 0x200000;

    /**
     * (Internal to VTEX?)
     */
    public static final int FlagAlphaTestMipmapGeneration = 0x400000;

    /**
     * Do not buffer for Video Processing, generally render distance.
     */
    public static final int FlagNoDepthBuffer = 0x800000;

    /**
     * Use NICE filtering to generate mipmaps. (Internal to VTEX?)
     */
    public static final int FlagNiceFiltered = 0x1000000;

    /**
     * Clamp U coordinates (for volumetric textures).
     */
    public static final int FlagClampU = 0x2000000;

    /**
     * Usable as a vertex texture.
     */
    public static final int FlagVertexTexture = 0x4000000;

    /**
     * Texture is a SSBump. (SSB)
     */
    public static final int FlagSSBump = 0x8000000;

    /**
     * Clamp to border colour on all texture coordinates.
     */
    public static final int FlagBorder = 0x20000000;
}
