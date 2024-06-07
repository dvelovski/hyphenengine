package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class mstudiobbox_t extends BaseStruct implements StructWrapper<mstudiobbox_t> {
    public int bone;
    public int group;
    vector_t bbmin;
    vector_t bbmax;
    int szhitboxnameindex;

    @Override
    public mstudiobbox_t parse(ByteBuffer in) {
        bone = in.getInt();
        group = in.getInt();
        bbmin = new vector_t().parse(in);
        bbmax = new vector_t().parse(in);

        szhitboxnameindex = in.getInt();
        skip(in, Integer.BYTES * 8);

        /*System.out.println("struct offset: " + structOffset + " / hitbox name index: " + szhitboxnameindex);
        System.out.println("added: " + (structOffset + szhitboxnameindex));

        System.out.println("HITBOX NAME: " + SourceInterop.fetchNullTerminatedString(in, structOffset + this.szhitboxnameindex, 64));*/


        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
