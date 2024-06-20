package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioanimsections_t extends BaseStruct implements StructWrapper<mstudioanimsections_t> {
    public static final int SIZE = 8;

    public int animblock;
    public int animindex;

    @Override
    public mstudioanimsections_t parse(ByteBuffer in) {
        animblock = in.getInt();
        animindex = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
