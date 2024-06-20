package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class lump_t extends BaseStruct implements StructWrapper<lump_t> {
    /* Offset into the file */
    public int fileOfs;

    /* Length, in bytes, of this lump in the file. */
    public int fileLen;

    public int version;
    public int fourCC;

    public int lumpID;

    @Override
    public lump_t parse(ByteBuffer in) {
        fileOfs = in.getInt();
        fileLen = in.getInt();
        version = in.getInt();
        fourCC = in.getInt();
        //System.out.println("\tfile offset: " + fileOfs + " | length: " + fileLen + " | version: " + version);
        return this;
    }

    @Override
    public int sizeOf() {
        return 16;
    }

    public void printFourCC() {
        System.out.println(fourCC);
    }
}
