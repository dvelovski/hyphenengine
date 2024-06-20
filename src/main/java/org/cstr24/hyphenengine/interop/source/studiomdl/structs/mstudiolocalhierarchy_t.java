package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudiolocalhierarchy_t extends BaseStruct implements StructWrapper<mstudiolocalhierarchy_t> {
    public int iBone;
    public int iNewParent;

    public float start;
    public float peak;
    public float tail;
    public float end;

    public int iStart;

    public int localanimindex;

    @Override
    public mstudiolocalhierarchy_t parse(ByteBuffer in) {
        iBone = in.getInt();
        iNewParent = in.getInt();

        start = in.getFloat();
        peak = in.getFloat();
        tail = in.getFloat();
        end = in.getFloat();

        iStart = in.getInt();

        localanimindex = in.getInt();

        skip(in, Integer.BYTES * 4); //int unused[4];
        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
