package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class ddispinfo_t_v17 extends ddispinfo_t {
    public static final int SIZE = 172;

    @Override
    public ddispinfo_t_v17 parse(ByteBuffer in) {
        startPosition = new vector_t().parse(in);

        dispVertStart = in.getInt();
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
    public int getTriangleTagCount() {
        return 0;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
