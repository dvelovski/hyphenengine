package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class dmodel_t extends BaseStruct implements StructWrapper<dmodel_t> {
    public static final int SIZE = 48;
    public vector_t mins;
    public vector_t maxs;
    public vector_t origin;
    public int headNode;
    public int firstFace;
    public int numFaces;

    @Override
    public dmodel_t parse(ByteBuffer in) {
        mins = new vector_t().parseStruct(in);
        maxs = new vector_t().parseStruct(in);
        origin = new vector_t().parseStruct(in);
        headNode = in.getInt();
        firstFace = in.getInt();
        numFaces = in.getInt();
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
