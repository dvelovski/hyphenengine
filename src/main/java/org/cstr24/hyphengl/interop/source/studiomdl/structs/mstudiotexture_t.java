package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudiotexture_t extends BaseStruct implements StructWrapper<mstudiotexture_t> {
    public int sznameindex;
    public int flags;

    public String textureName;

    @Override
    public mstudiotexture_t parse(ByteBuffer in) {
        int structPos = in.position();

        sznameindex = in.getInt();
        flags = in.getInt();
        skip(in, Integer.BYTES * 4); //skip int used; int unused1; two pointers (mutable IMaterial *material; mutable void *clientmaterial)
        skip(in, Integer.BYTES * 10); //int unused[10];

        textureName = SourceInterop.fetchNullTerminatedString(in, structPos + sznameindex, 64);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
