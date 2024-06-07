package org.cstr24.hyphengl.interop.source.vbsp.structs;

import java.nio.ByteBuffer;

public class dleaf_t_v19 extends dleaf_t {
    public static final int SIZE = 56;
    //for release version of HL2 only - CompressedLightCube - 24 bytes
    //plus 2 bytes padding

    public CompressedLightCube ambientLighting;

    @Override
    public dleaf_t parse(ByteBuffer in) {
        super.parse(in);

        ambientLighting = new CompressedLightCube().parse(in);
        skip(in, 2); //short padding - padding to 4-byte boundary

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
