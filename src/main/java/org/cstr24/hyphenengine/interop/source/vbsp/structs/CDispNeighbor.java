package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class CDispNeighbor extends BaseStruct implements StructWrapper<CDispNeighbor> {
    public static final int SIZE = CDispSubNeighbor.SIZE * 2;

    public CDispSubNeighbor[] subNeighbors = new CDispSubNeighbor[2];

    @Override
    public CDispNeighbor parse(ByteBuffer in) {
        subNeighbors[0] = new CDispSubNeighbor().parse(in);
        subNeighbors[1] = new CDispSubNeighbor().parse(in);
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }

    public boolean isValid() {
        return subNeighbors[0].isValid() || subNeighbors[1].isValid();
    }
}
