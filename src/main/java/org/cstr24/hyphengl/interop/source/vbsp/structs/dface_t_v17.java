package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.vbsp.Constants;

import java.nio.ByteBuffer;

public class dface_t_v17 extends dface_t {
    public static final int SIZE = 68;
    //we do NOT have numPrimitives or firstPrimitiveID which is why it's 4 bytes smaller than dface_18 but still has the extra 16 int bytes for average light colours

    public int[] averageLightColour = new int[Constants.MAX_LIGHTMAPS];

    @Override
    public dface_t parse(ByteBuffer in) {
        for (int i = 0; i < averageLightColour.length; i++) {
            averageLightColour[i] = in.getInt();
        }

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
        smoothingGroup = in.getInt();

        return this;
    }

    public int sizeOf() {
        return SIZE;
    }
}
