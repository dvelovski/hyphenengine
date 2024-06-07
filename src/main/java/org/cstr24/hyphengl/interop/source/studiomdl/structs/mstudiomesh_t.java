package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.structs.vector_t;

import java.nio.ByteBuffer;

public class mstudiomesh_t extends BaseStruct implements StructWrapper<mstudiomesh_t> {
    public int material;
    public int modelindex;

    public int numvertices;
    public int vertexoffset;

    public int numflexes;
    public int flexindex;

    public int materialtype;
    public int materialparam;

    public int meshid;

    public vector_t center;

    public mstudio_meshvertexdata_t vertexdata;

    @Override
    public mstudiomesh_t parse(ByteBuffer in) {
        material = in.getInt();
        modelindex = in.getInt();

        numvertices = in.getInt();
        vertexoffset = in.getInt();

        numflexes = in.getInt();
        flexindex = in.getInt();

        materialtype = in.getInt();
        materialparam = in.getInt();

        meshid = in.getInt();

        center = new vector_t().parseStruct(in);

        vertexdata = new mstudio_meshvertexdata_t().parseStruct(in);

        skip(in, Integer.BYTES * 8);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
