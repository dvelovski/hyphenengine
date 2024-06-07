package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import java.nio.ByteBuffer;

public class StripHeader_t_v49 extends StripHeader_t{
    public int numTopologyIndices;
    public int topologyOffset;

    @Override
    public StripHeader_t parse(ByteBuffer in) {
        super.parse(in);

        numTopologyIndices = in.getInt();
        topologyOffset = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return super.sizeOf() + 8; //2 extra int fields
    }
}
