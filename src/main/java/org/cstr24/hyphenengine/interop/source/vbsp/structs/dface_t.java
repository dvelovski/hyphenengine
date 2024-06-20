package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.vbsp.Constants;

import java.nio.ByteBuffer;

public class dface_t extends BaseStruct implements StructWrapper<dface_t> {
    public static final int SIZE = 56;

    public int planeNum;
    //originally an unsigned short, converted to int range

    public byte side;
    public byte onNode;

    public int firstEdge;

    public short numEdges;
    public short textureInfo;
    public short displacementInfo;
    public int surfaceFogVolumeID;

    public byte[] styles = new byte[Constants.MAX_LIGHTMAPS];

    public int lightOffset; //offset into lightmap lump

    public float area; //face area in units ^ 2

    public int[] lightmapTextureMinsInLuxels = new int[2];
    public int[] lightmapTextureSizeInLuxels = new int[2];
    public int originalFace;

    public int numPrimitives;
    //originally unsigned short
    public int firstPrimitiveID;
    //originally unsigned short

    public int smoothingGroup; //originally unsigned int. I don't know if we need 'unsigned int's full range

    @Override
    public dface_t parse(ByteBuffer in) {
        planeNum = uShortToInt(in.getShort());

        side = in.get();
        onNode = in.get();

        firstEdge = in.getInt();

        numEdges = in.getShort();
        textureInfo = in.getShort();
        displacementInfo = in.getShort();
        surfaceFogVolumeID = in.getShort();

        in.get(styles);

        lightOffset = in.getInt();

        area = in.getFloat();

        lightmapTextureMinsInLuxels[0] = in.getInt();
        lightmapTextureMinsInLuxels[1] = in.getInt();

        lightmapTextureSizeInLuxels[0] = in.getInt();
        lightmapTextureSizeInLuxels[1] = in.getInt();

        originalFace = in.getInt();

        numPrimitives = uShortToInt(in.getShort());
        firstPrimitiveID = uShortToInt(in.getShort());

        smoothingGroup = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
