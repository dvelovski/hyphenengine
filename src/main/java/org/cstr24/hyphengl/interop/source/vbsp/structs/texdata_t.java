package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class texdata_t extends BaseStruct implements StructWrapper<texdata_t> {
    public static final int SIZE = 32;

    public vector_t reflectivity;
    public int nameStringTableID;
    public int width;
    public int height;
    public int viewWidth;
    public int viewHeight;

    @Override
    public texdata_t parse(ByteBuffer in) {
        reflectivity = new vector_t().parse(in);
        nameStringTableID = in.getInt();

        width = in.getInt();
        height = in.getInt();

        viewWidth = in.getInt();
        viewHeight = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
