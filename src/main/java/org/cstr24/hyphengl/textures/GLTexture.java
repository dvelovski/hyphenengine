package org.cstr24.hyphengl.textures;

import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.EXTTextureCompressionS3TC.*;
import static org.lwjgl.opengl.EXTTextureSRGB.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public interface GLTexture {
    default int filterModeToEnum(FilterMode mode){
        switch (mode){
            case Linear -> {
                return GL11.GL_LINEAR;
            }
            case NearestMipmapNearest -> {
                return GL_NEAREST_MIPMAP_NEAREST;
            }
            case LinearMipmapNearest -> {
                return GL_LINEAR_MIPMAP_NEAREST;
            }
            case NearestMipmapLinear -> {
                return GL_NEAREST_MIPMAP_LINEAR;
            }
            case LinearMipmapLinear -> {
                return GL_LINEAR_MIPMAP_LINEAR;
            }
            default -> {
                return GL_NEAREST; //the default
            }
        }
    }
    default int wrapModeToEnum(WrapMode mode){
        switch (mode){
            case MirroredRepeat -> {
                return GL_MIRRORED_REPEAT;
            }
            case ClampToEdge -> {
                return GL_CLAMP_TO_EDGE;
            }
            case ClampToBorder -> {
                return GL_CLAMP_TO_BORDER;
            }
            default -> {
                return GL_REPEAT; //the default behaviour
            }
        }
    }
    default int imageFormatToEnum(ImageFormat format){
        switch (format){
            case DXT1_RGB -> {
                return GL_COMPRESSED_RGB_S3TC_DXT1_EXT;
            }
            case DXT1_RGBA -> {
                return GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
            }
            case DXT1_sRGB -> {
                return GL_COMPRESSED_SRGB_S3TC_DXT1_EXT;
            }
            case DXT1_sRGBA -> {
                return GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT1_EXT;
            }
            case DXT3_RGBA -> {
                return GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
            }
            case DXT3_sRGBA -> {
                return GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT3_EXT;
            }
            case DXT5_RGBA -> {
                return GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
            }
            case DXT5_sRGBA -> {
                return GL_COMPRESSED_SRGB_ALPHA_S3TC_DXT5_EXT;
            }
        }
        return -1;
    }
}
