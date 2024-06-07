package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

public class Vector48 extends BaseStruct implements StructWrapper<Vector48> {
    public static final int SIZE = 6;

    public Vector3f value;
    @Override
    public Vector48 parse(ByteBuffer in) {
        float x, y, z;
        x = halfPrecisionToFloat(in.getShort());
        y = halfPrecisionToFloat(in.getShort());
        z = halfPrecisionToFloat(in.getShort());

        value = new Vector3f(x, y, z);

        return this;
    }
    public Vector3f toVector(){
        return value;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
