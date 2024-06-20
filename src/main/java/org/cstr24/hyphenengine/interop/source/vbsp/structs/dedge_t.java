package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class dedge_t extends BaseStruct implements StructWrapper<dedge_t> {
    public static final int SIZE = 4;

    public int[] indices = new int[2];
    /* originally unsigned shorts, converted to int range */

    @Override
    public dedge_t parse(ByteBuffer in) {
        indices[0] = uShortToInt(in.getShort());
        indices[1] = uShortToInt(in.getShort());
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
