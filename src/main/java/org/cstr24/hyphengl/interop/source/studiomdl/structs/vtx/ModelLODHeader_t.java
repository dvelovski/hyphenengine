package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ModelLODHeader_t extends BaseStruct implements StructWrapper<ModelLODHeader_t> {
    public static final int SIZE = 12;

    public int numMeshes;
    public int meshOffset;

    public float switchPoint;

    public ArrayList<MeshHeader_t> meshHeaders;


    @Override
    public ModelLODHeader_t parse(ByteBuffer in) {
        numMeshes = in.getInt();
        meshOffset = in.getInt();

        switchPoint = in.getFloat();

        meshHeaders = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
