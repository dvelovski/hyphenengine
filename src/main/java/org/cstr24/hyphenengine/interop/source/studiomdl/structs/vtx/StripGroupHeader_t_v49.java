package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx;

import java.nio.ByteBuffer;

public class StripGroupHeader_t_v49 extends StripGroupHeader_t{
    public int numTopologyIndices;
    public int topologyOffset;

    @Override
    public StripGroupHeader_t parse(ByteBuffer in) {
        super.parse(in);

        numTopologyIndices = in.getInt();
        topologyOffset = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return super.sizeOf() + 8; //plus 2 int fields
    }
}
