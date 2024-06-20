package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioanimblock_t extends BaseStruct implements StructWrapper<mstudioanimblock_t> {
    public static final int SIZE = 8;
    public int datastart;
    public int dataend;

    @Override
    public mstudioanimblock_t parse(ByteBuffer in) {
        datastart = in.getInt();
        dataend = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
