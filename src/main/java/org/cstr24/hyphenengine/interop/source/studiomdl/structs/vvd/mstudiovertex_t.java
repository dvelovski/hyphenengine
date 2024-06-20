package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;
import org.joml.Vector2f;

import java.nio.ByteBuffer;

public class mstudiovertex_t extends BaseStruct implements StructWrapper<mstudiovertex_t> {
    public static final int SIZE = 48;

    public mstudioboneweight_t m_BoneWeights;
    public vector_t m_vecPosition;
    public vector_t m_vecNormal;
    public Vector2f m_vecTexCoord;

    @Override
    public mstudiovertex_t parse(ByteBuffer in) {
        m_BoneWeights = new mstudioboneweight_t().parseStruct(in);
        m_vecPosition = new vector_t().parse(in);
        m_vecNormal = new vector_t().parse(in);
        m_vecTexCoord = new Vector2f(in.getFloat(), in.getFloat());

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
