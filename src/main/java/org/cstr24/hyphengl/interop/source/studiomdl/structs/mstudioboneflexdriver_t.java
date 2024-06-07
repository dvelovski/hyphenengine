package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioboneflexdriver_t extends BaseStruct implements StructWrapper<mstudioboneflexdriver_t> {
    public static final int SIZE = 24;

    public int boneIndex; //bone to drive flex controller
    public int controlCount; ///number of flex controllers being driven
    public int controlIndex; //index into data where controllers are (relative to this)

    @Override
    public mstudioboneflexdriver_t parse(ByteBuffer in) {
        boneIndex = in.getInt();
        controlCount = in.getInt();
        controlIndex = in.getInt();

        skip(in, Integer.BYTES * 3); //int unused[3];
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
