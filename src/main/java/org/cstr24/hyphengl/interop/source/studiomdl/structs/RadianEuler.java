package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class RadianEuler extends BaseStruct implements StructWrapper<RadianEuler> {
    public static final int SIZE = 12;
    public float x;
    public float y;
    public float z;

    @Override
    public RadianEuler parse(ByteBuffer in) {
        x = in.getFloat();
        y = in.getFloat();
        z = in.getFloat();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
