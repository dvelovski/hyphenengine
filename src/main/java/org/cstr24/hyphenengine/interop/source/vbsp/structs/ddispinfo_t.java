package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

//TODO ddispinfo_t_v22

//TODO ddispinfo_t_v23

public class ddispinfo_t extends BaseStruct implements StructWrapper<ddispinfo_t> {
    public static final int SIZE = 176;
    /**
     * members (including padded DispNeighbor structs) = 174 bytes
     * but pad to 2 to align to multiple of 8
     * i sense checked all of these by compiling structs in MSVC
     * i checked my manual calculations of 174 bytes using #pragma pack(1)
     * without that directive sizeof yields 176
     * <p>
     * i am going to assume padding was added at the END of the struct
     * and not in the middle (between elements)
     * or i will go nuts
     **/

    public vector_t startPosition; //start position, used for orientation
    public int dispVertStart; //index into LUMP_DISP_VERTS
    public int dispTriStart; //index into LUMP_DISP_TRIS
    public int power; //power - indicates size of surface
    public int minTess; //minimum tesselation allowed
    public float smoothingAngle; //lighting smoothing angle
    public int contents; //surface contents
    public int mapFace; //which map face this displacement comes from
    // ^ the above is declared as an unsigned short, we don't have those, so should declare as int
    public int lightmapAlphaStart; //index into ddisplightmapalpha
    public int lightmapSamplePositionStart; //index into LUMP_DISP_LIGHTMAP_SAMPLE_POSITIONS
    public CDispNeighbor[] edgeNeighbors = new CDispNeighbor[4];
    public CDispCornerNeighbors[] cornerNeighbors = new CDispCornerNeighbors[4];
    public int[] allowedVerts = new int[10];

    public int getPowerSize() {
        return 1 << power;
    }

    public int getVertexCount() {
        return (getPowerSize() + 1) * (getPowerSize() + 1);
    }

    public int getTriangleTagCount() {
        return 2 * getPowerSize() * getPowerSize();
    }

    @Override
    public ddispinfo_t parse(ByteBuffer in) {
        startPosition = new vector_t().parse(in);

        dispVertStart = in.getInt();
        dispTriStart = in.getInt();
        power = in.getInt();
        minTess = in.getInt();

        smoothingAngle = in.getFloat();

        contents = in.getInt();
        mapFace = uShortToInt(in.getShort()); //convert from signed short range to unsigned range

        lightmapAlphaStart = in.getInt();
        lightmapSamplePositionStart = in.getInt();

        //skip(in, 90);
        edgeNeighbors[0] = new CDispNeighbor().parse(in);
        edgeNeighbors[1] = new CDispNeighbor().parse(in);
        edgeNeighbors[2] = new CDispNeighbor().parse(in);
        edgeNeighbors[3] = new CDispNeighbor().parse(in);

        cornerNeighbors[0] = new CDispCornerNeighbors().parse(in);
        cornerNeighbors[1] = new CDispCornerNeighbors().parse(in);
        cornerNeighbors[2] = new CDispCornerNeighbors().parse(in);
        cornerNeighbors[3] = new CDispCornerNeighbors().parse(in);

        for (int i = 0; i < allowedVerts.length; i++) {
            allowedVerts[i] = in.getInt();
        }

        skip(in, 2);

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
