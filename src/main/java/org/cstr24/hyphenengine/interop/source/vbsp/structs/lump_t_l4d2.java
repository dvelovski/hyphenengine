package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import java.nio.ByteBuffer;

public class lump_t_l4d2 extends lump_t {
    /* The fields in lump_t have a different order. */

    @Override
    public lump_t parse(ByteBuffer in) {
        //read in a different order
        this.version = in.getInt();
        this.fileOfs = in.getInt();
        this.fileLen = in.getInt();
        this.fourCC = in.getInt();
        //System.out.println("\tfile offset: " + fileOfs + " | length: " + fileLen + " | version: " + version);
        return this;
    }
}
