package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioanimvalue_t extends BaseStruct implements StructWrapper<mstudioanimvalue_t> {
    public static final int SIZE = 2;
    public byte valid;
    public byte total;

    public short value;

    @Override
    public mstudioanimvalue_t parse(ByteBuffer in) {
        this.valid = in.get();
        this.total = in.get();

        in.position(this.structPos);
        this.value = in.getShort();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
