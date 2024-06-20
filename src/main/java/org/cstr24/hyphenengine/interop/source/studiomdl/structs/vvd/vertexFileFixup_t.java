package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class vertexFileFixup_t extends BaseStruct implements StructWrapper<vertexFileFixup_t> {
    public int lod;
    public int sourceVertexID;
    public int numVertexes;

    @Override
    public vertexFileFixup_t parse(ByteBuffer in) {
        lod = in.getInt();
        sourceVertexID = in.getInt();
        numVertexes = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
