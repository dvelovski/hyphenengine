package org.cstr24.hyphenengine.textures;

public class TextureAllocationInfo {
    public int width;
    public int height;
    public int depth;

    public ImageFormat internalFormat;

    public static TextureAllocationInfo Texture2D(int _width, int _height, ImageFormat format){
        var aInfo = new TextureAllocationInfo();
        aInfo.width = _width;
        aInfo.height = _height;
        aInfo.internalFormat = format;
        return aInfo;
    }
}
