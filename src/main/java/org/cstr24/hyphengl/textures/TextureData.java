package org.cstr24.hyphengl.textures;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class TextureData {
    public int imageWidth;
    public int imageHeight;
    public int imageDepth;

    public String texturePath = "";
    public String textureName = "";

    public int internalFormat; //TODO migrate to an enum
    public ImageFormat format;
    public MipData[][] mipData; //face | mipLevel


    public TextureData(String _txName){
        this.textureName = _txName;
    }
    public TextureData(String _txPath, String _txName){
        this.texturePath = _txPath;
        this.textureName = _txName;
    }
    public void setMipData(MipData[][] _data){
        this.mipData = _data;
    }

    public static class MipData {
        public ByteBuffer data;
        public int width;
        public int height;

        public CubemapFace face = CubemapFace.None;

        public int level;
    }
}
