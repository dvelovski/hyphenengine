package org.cstr24.hyphenengine.textures;

import org.cstr24.hyphenengine.rendering.ColourSpace;

public enum ImageFormat {
    Unknown(3, false, ColourSpace.RGB),

    DXT1_RGB(3, true, ColourSpace.RGB),
    DXT1_RGBA(4, true, ColourSpace.RGB),
    DXT1_sRGB(3, true, ColourSpace.sRGB),
    DXT1_sRGBA(4, true, ColourSpace.sRGB),

    DXT3_RGBA(4, true, ColourSpace.RGB),
    DXT3_sRGBA(4, true, ColourSpace.sRGB),

    DXT5_RGBA(4, true, ColourSpace.RGB),
    DXT5_sRGBA(4, true, ColourSpace.RGB);

    public final int channels;
    public final boolean compressed;
    public final ColourSpace colourSpace;
    private String formatName;

    ImageFormat(int _channels, boolean _compressed, ColourSpace _space){
        this.channels = _channels;
        this.compressed = _compressed;
        this.colourSpace = _space;
    }
    ImageFormat setFormatName(String _name){
        formatName = _name;
        return this;
    }

    public String getName(){
        return this.formatName.isEmpty() ? this.name() : this.formatName;
    }

    public int getCompressedBlockSize(){
        if (compressed){
            switch (this){
                case DXT1_RGB, DXT1_RGBA, DXT1_sRGB, DXT1_sRGBA -> {
                    return 8;
                }
                default -> {
                    return 16;
                }
            }
        }else{
            return 1; //i'd return -1 or 0 BUT i don't want to end up in a situation where someone's got negative file size
        }
    }
    public int computeCompressedMipSize(int width, int height){
        if (compressed){
            int bSize = getCompressedBlockSize();
            return bSize * ((width + 3) / 4) * ((height + 3) / 4);
        }else{
            return width * height; //i'd return -1 or 0 BUT i don't want to end up in a situation where someone's got negative file size
        }
    }
}
