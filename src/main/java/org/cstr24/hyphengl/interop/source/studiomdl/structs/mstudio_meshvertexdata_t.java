package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModel;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudio_meshvertexdata_t extends BaseStruct implements StructWrapper<mstudio_meshvertexdata_t> {
    public int[] numLODVertexes;

    @Override
    public mstudio_meshvertexdata_t parse(ByteBuffer in) {
        skip(in, Integer.BYTES); //mstudio_meshvertexdata_t is made up of a few functions. the first member is a pointer. skip it.

        numLODVertexes = new int[StudioModel.MAX_NUM_LODS];
        for (int i = 0; i < numLODVertexes.length; i++){
            numLODVertexes[i] = in.getInt();
        }

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
