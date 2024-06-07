package org.cstr24.hyphengl.interop.source.vtf;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.rendering.ColourSpace;
import org.cstr24.hyphengl.textures.ImageFormat;

import java.nio.ByteBuffer;

public class VTFFile extends BaseStruct implements StructWrapper<VTFFile> {
    public static final int SIZE = 80; //as of v7.3 and up

    public int signature;

    public int versionMajor, versionMinor;

    public int headerSize;

    public int width; //width of the largest mipmap in pixels
    public int height; //height of the largest mipmap in pixels

    public int flags;

    //originally declared as unsigned short
    public int frames; //number of frames, if animated. if not animated, it's 1.
    //originally declared as unsigned short
    public int firstFrame; //first frame in animation, 0 based, and if it's -1 in a map older than 7.5 it means there are 7 faces, not 6 in an environment map

    public float[] reflectivity = new float[3];

    public float bumpmapScale;
    //originally declared as unsigned int, don't do anything at the moment
    public int highResImageFormat;
    //originally declared as unsigned char
    public short mipmapCount;

    public int lowResImageFormat;
    //originally dclared as unsigned char
    public short lowResImageWidth;
    //originally declared as unsigned char
    public short lowResImageHeight;

    //7.2+
    public int depth;

    //7.3+
    public int numResources;
    public ResourceEntryInfo[] resources;

    public ImageFormat imageFormat;
    public ColourSpace colourSpace;

    @Override
    public VTFFile parse(ByteBuffer in) {
        int startPos = in.position();

        signature = in.get() | in.get() << 8 | in.get() << 16 | in.get() << 24;

        versionMajor = in.getInt();
        versionMinor = in.getInt();

        headerSize = in.getInt();

        width = uShortToInt(in.getShort());
        height = uShortToInt(in.getShort());

        flags = in.getInt();

        frames = uShortToInt(in.getShort());
        firstFrame = uShortToInt(in.getShort());

        skip(in, 4); //padding0 - skip 4 bytes

        reflectivity = floatArray(in, reflectivity);

        skip(in, 4); //padding1 - skip 4 bytes

        bumpmapScale = in.getFloat();

        highResImageFormat = in.getInt();

        mipmapCount = uCharToShort(in.get());

        lowResImageFormat = in.getInt(); //always DXT1
        lowResImageWidth = uCharToShort(in.get());
        lowResImageHeight = uCharToShort(in.get());

        //System.out.println("version: " + versionMajor + ", " + versionMinor);


        if (versionMajor >= 7){
            if (versionMinor >= 2){
                depth = uShortToInt(in.getShort());
                if (versionMinor >= 3){
                    skip(in, 3);

                    numResources = in.getInt();
                    skip(in, 8); //"necessary on certain compilers" sure seems necesary all the time to me

                    resources = new ResourceEntryInfo[numResources];

                    for (int r = 0; r < numResources; r++){
                        resources[r] = new ResourceEntryInfo().parse(in);
                    }
                }
            }
        }

        colourSpace = getColourSpace();
        imageFormat = getImageFormat();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }

    public int getHighResDataOffset(){
        if (versionMinor < 3){
            //System.out.println("lowres " + lowResImageWidth + " x " + lowResImageHeight);
            return this.headerSize + (ImageFormat.DXT1_RGB.computeCompressedMipSize(lowResImageWidth, lowResImageHeight));
        }else{
            return getResource(ResourceEntryInfo.HIGH_RES_DATA).offset;
        }
    }
    public ResourceEntryInfo getResource(int type){
        for (ResourceEntryInfo resource : resources) {
            if (resource.typeCode == type){
                return resource;
            }
        }
        return null;
    }

    public boolean isEnvironmentMap(){
        return (this.flags & VTFFlags.FlagEnvironmentMap) != 0;
    }

    public int getNumFaces(){
        if ((this.flags & VTFFlags.FlagEnvironmentMap) != 0){
            if (versionMajor == 7 && versionMinor < 5){
                if (firstFrame == -1){
                    return 7;
                }
            }
            return 6;
        }else{
            return 1;
        }
    }

    public ImageFormat getImageFormat(){
        switch (highResImageFormat){
            case VTFFormat.IMAGE_FORMAT_DXT1 -> {
                return (colourSpace == ColourSpace.sRGB ? ImageFormat.DXT1_sRGB : ImageFormat.DXT1_RGB);
            }
            case VTFFormat.IMAGE_FORMAT_DXT1_ONEBITALPHA -> {
                return (colourSpace == ColourSpace.sRGB ? ImageFormat.DXT1_sRGBA : ImageFormat.DXT1_RGBA);
            }
            case VTFFormat.IMAGE_FORMAT_DXT3 -> {
                return (colourSpace == ColourSpace.sRGB ? ImageFormat.DXT3_sRGBA : ImageFormat.DXT3_RGBA);
            }
            case VTFFormat.IMAGE_FORMAT_DXT5 -> {
                return (colourSpace == ColourSpace.sRGB ? ImageFormat.DXT5_sRGBA : ImageFormat.DXT5_RGBA);
            }
        }
        return ImageFormat.Unknown;
    }
    public ColourSpace getColourSpace(){
        if ((this.flags & VTFFlags.FlagSRGB) != 0){
            return ColourSpace.sRGB;
        }else{
            return ColourSpace.RGB;
        }
    }

    public static class ResourceEntryInfo extends BaseStruct implements StructWrapper<ResourceEntryInfo> {
        public static final int THUMB_DATA = 0;
        public static final int HIGH_RES_DATA = 1;
        public static final int PARTICLE_DATA = 2;
        public static final int CRC_CHECK_DATA = 3;
        public static final int LOD_CONTROL_DATA = 4;
        public static final int EXT_VTF_FLAGS = 5;
        public static final int KEY_VALUE_DATA = 6;

        //public String identifiedType;
        public short flags;
        public int offset;
        public int typeCode;

        @Override
        public ResourceEntryInfo parse(ByteBuffer in) {
            short[] tag = uByteArray(in, new short[3]);
            flags = uCharToShort(in.get());
            offset = in.getInt();

            if (tag[0] == '\u0001' && tag[1] == 0 && tag[2] == 0){
                typeCode = THUMB_DATA;
            }else if (tag[0] == '\u0030' && tag[1] == 0 && tag[2] == 0){
                typeCode = HIGH_RES_DATA;
            }else if (tag[0] == '\u0010' && tag[1] == 0 && tag[2] == 0){
                typeCode = PARTICLE_DATA;
            }else if (tag[0] == 'C' && tag[1] == 'R' && tag[2] == 'C'){
                typeCode = CRC_CHECK_DATA;
            }else if (tag[0] == 'L' && tag[1] == 'O' && tag[2] == 'D'){
                typeCode = LOD_CONTROL_DATA;
            }else if (tag[0] == 'T' && tag[1] == 'S' && tag[2] == '0'){
                typeCode = EXT_VTF_FLAGS;
            }else if (tag[0] == 'K' && tag[1] == 'V' && tag[2] == 'D'){
                typeCode = KEY_VALUE_DATA;
            }else{
                typeCode = -1;
            }

            return this;
        }
        public String getIdentifiedTypeName(){
            switch (typeCode){
                case THUMB_DATA -> {return "Low-res (thumbnail) image data";}
                case HIGH_RES_DATA -> {return "High-res image data";}
                case PARTICLE_DATA -> {return "Animated particle sheet data";}
                case CRC_CHECK_DATA -> {return "CRC checksum";}
                case LOD_CONTROL_DATA -> {return "Texture LOD control information";}
                case EXT_VTF_FLAGS -> {return "Extended VTF flags";}
                case KEY_VALUE_DATA -> {return "KeyValues data";}
                default -> {return "Unknown";}
            }
        }

        @Override
        public int sizeOf() {
            return 0;
        }
    }
}
