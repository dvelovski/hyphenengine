package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import java.nio.ByteBuffer;

public class dleaf_t_all extends dleaf_t {
    @Override
    public dleaf_t parse(ByteBuffer in) {
        super.parse(in);
        skip(in, 2);
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE; //32 lol
    }
}
