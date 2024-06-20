package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudiobonecontroller_t extends BaseStruct implements StructWrapper<mstudiobonecontroller_t> {
    public int bone; //-1 == 0
    public int type; //X, Y, Z, XR, YR, ZR, M
    public float start;
    public float end;
    public int rest; //byte index value at rest
    public int inputfield; //0-3 user set controller, 4 mouth

    @Override
    public mstudiobonecontroller_t parse(ByteBuffer in) {
        bone = in.getInt();
        type = in.getInt();

        start = in.getFloat();
        end = in.getFloat();

        rest = in.getInt();
        inputfield = in.getInt();

        skip(in, Integer.BYTES * 8); //skip int unused[8];

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
