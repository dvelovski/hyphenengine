package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class surfedge_t extends BaseStruct implements StructWrapper<surfedge_t> {
    public static final int SIZE = 4;
    public int edgeIndex;

    @Override
    public surfedge_t parse(ByteBuffer in) {
        edgeIndex = in.getInt();
        /* absolute value of this number is an index into the edge array -
            if positive: means edge is defined from first-second vertex
            otherwise, from second-first vertex.
         */
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
