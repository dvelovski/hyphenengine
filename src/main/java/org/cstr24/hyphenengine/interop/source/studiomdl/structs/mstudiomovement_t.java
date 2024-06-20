package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class mstudiomovement_t extends BaseStruct implements StructWrapper<mstudiomovement_t> {
    public int endframe;
    public int motionflags;

    public float v0; //velocity at start of block
    public float v1; //velocity at end of block
    public float angle; //YAW rotation at the end of this blocks movement

    public vector_t vector; //movement vector relative to this block's initial angle
    public vector_t position; //relative to start of animation

    @Override
    public mstudiomovement_t parse(ByteBuffer in) {
        endframe = in.getInt();
        motionflags = in.getInt();

        v0 = in.getFloat();
        v1 = in.getFloat();
        angle = in.getFloat();

        vector = new vector_t().parse(in);
        position = new vector_t().parse(in);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
