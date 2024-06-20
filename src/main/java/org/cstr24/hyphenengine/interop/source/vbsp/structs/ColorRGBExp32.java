package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public final class ColorRGBExp32 extends BaseStruct implements StructWrapper<ColorRGBExp32> {
    public static final int SIZE = 4;

    public short r;
    public short g;
    public short b;
    public char exponent;


    @Override
    public ColorRGBExp32 parse(ByteBuffer in) {
        r = uByteToShort(in.get());
        g = uByteToShort(in.get());
        b = uByteToShort(in.get());

        exponent = (char) in.get();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
