package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioflex_t extends BaseStruct implements StructWrapper<mstudioflex_t> {
    public int flexdesc;

    public float target0;
    public float target1;
    public float target2;
    public float target3;

    public int numverts;
    public int vertindex;

    public int flexpair;
    public short vertanimtype;

    @Override
    public mstudioflex_t parse(ByteBuffer in) {
        flexdesc = in.getInt();

        target0 = in.getFloat();
        target1 = in.getFloat();
        target2 = in.getFloat();
        target3 = in.getFloat();

        numverts = in.getInt();
        vertindex = in.getInt();

        flexpair = in.getInt();
        vertanimtype = uCharToShort(in.get());

        skip(in, 3); //unsigned char unusedchar[3];
        skip(in, Integer.BYTES * 6); //int unused[6];

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
