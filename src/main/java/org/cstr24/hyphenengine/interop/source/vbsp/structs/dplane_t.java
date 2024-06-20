package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class dplane_t extends BaseStruct implements StructWrapper<dplane_t> {
    public static final int SIZE = 20;

    public vector_t normal; //normal vector
    public float dist; //distance from origin
    public int type; //appears unused

    @Override
    public dplane_t parse(ByteBuffer in) {
        normal = new vector_t().parse(in).swapSpace(); //Swap space occurence
        dist = in.getFloat();
        type = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }


}
