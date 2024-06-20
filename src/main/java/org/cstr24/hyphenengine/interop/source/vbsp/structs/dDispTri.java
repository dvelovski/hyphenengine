package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class dDispTri extends BaseStruct implements StructWrapper<dDispTri> {
    public static final int SIZE = 2;
    public int tags; //declared as unsigned short

    @Override
    public dDispTri parse(ByteBuffer in) {
        tags = uShortToInt(in.getShort());
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
