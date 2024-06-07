package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioposeparamdesc_t extends BaseStruct implements StructWrapper<mstudioposeparamdesc_t> {
    public String name;

    public int flags;
    public float start;
    public float end;
    public float loop;

    @Override
    public mstudioposeparamdesc_t parse(ByteBuffer in) {
        int sznameindex = in.getInt();
        this.name = SourceInterop.fetchNullTerminatedString(in, this.structPos + sznameindex, 64);

        flags = in.getInt();
        start = in.getFloat();
        end = in.getFloat();
        loop = in.getFloat();

        //System.out.println("name: " + this.name);

        return this;
    }

    @Override
    public int sizeOf() {
        return 20;
    }
}
