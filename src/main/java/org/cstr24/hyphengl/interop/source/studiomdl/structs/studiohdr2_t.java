package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class studiohdr2_t extends BaseStruct implements StructWrapper<studiohdr2_t> {
    public int numscrbonetransform;
    public int srcbonetransformindex;

    public int illumpositionattachmentindex;

    public float flmaxeyedeflection;
    public int linearbone_index;

    public int sznameindex;

    public int m_nBoneFlexDriverCount;
    public int m_nBoneFlexDriverIndex;

    @Override
    public studiohdr2_t parse(ByteBuffer in) {
        numscrbonetransform = in.getInt();
        srcbonetransformindex = in.getInt();

        illumpositionattachmentindex = in.getInt();

        flmaxeyedeflection = in.getFloat();

        linearbone_index = in.getInt();

        sznameindex = in.getInt();

        m_nBoneFlexDriverCount = in.getInt();
        m_nBoneFlexDriverIndex = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
