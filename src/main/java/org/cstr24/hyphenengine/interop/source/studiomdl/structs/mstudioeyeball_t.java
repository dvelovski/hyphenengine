package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class mstudioeyeball_t extends BaseStruct implements StructWrapper<mstudioeyeball_t> {
    public int sznameindex;
    public int bone;

    public vector_t org;
    public float zoffset;
    public float radius;
    public vector_t up;
    public vector_t forward;

    public int texture;

    public float iris_scale;

    public int[] upperflexdesc;
    public int[] lowerflexdesc;
    public float[] uppertarget;
    public float[] lowertarget;

    public int upperlidflexdesc;
    public int lowerlidflexdesc;

    public boolean m_bNonFACS;


    @Override
    public mstudioeyeball_t parse(ByteBuffer in) {
        sznameindex = in.getInt();
        System.out.println(SourceInterop.readNullTerminatedString(in, this.structPos + sznameindex, 64, false));

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
