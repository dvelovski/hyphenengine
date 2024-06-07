package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public final class CompressedLightCube extends BaseStruct implements StructWrapper<CompressedLightCube> {
    public static final int SIZE = 24;

    public ColorRGBExp32[] colors = new ColorRGBExp32[6];

    @Override
    public CompressedLightCube parse(ByteBuffer in) {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new ColorRGBExp32().parse(in);
        }
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
