package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioflexdesc_t extends BaseStruct implements StructWrapper<mstudioflexdesc_t> {
    public int szFACSindex;
    @Override
    public mstudioflexdesc_t parse(ByteBuffer in) {
        szFACSindex = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
