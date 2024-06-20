package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class dDispVert extends BaseStruct implements StructWrapper<dDispVert> {
    public static final int SIZE = 20;

    public vector_t vec; //vector field defining displacement volume
    public float dist;
    public float alpha;

    @Override
    public dDispVert parse(ByteBuffer in) {
        vec = new vector_t().parse(in);
        dist = in.getFloat();
        alpha = in.getFloat();
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
