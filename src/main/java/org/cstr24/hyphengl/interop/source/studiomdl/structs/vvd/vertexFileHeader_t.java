package org.cstr24.hyphengl.interop.source.studiomdl.structs.vvd;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

import static org.cstr24.hyphengl.interop.source.studiomdl.StudioModel.MAX_NUM_LODS;

public class vertexFileHeader_t extends BaseStruct implements StructWrapper<vertexFileHeader_t> {
    public int id;
    public int version;
    public int checksum;
    public int numLODs;
    public int[] numLODVertexes;
    public int numFixups;
    public int fixupTableStart;
    public int vertexDataStart;
    public int tangentDataStart;

    @Override
    public vertexFileHeader_t parse(ByteBuffer in) {
        id = in.getInt();
        version = in.getInt();
        checksum = in.getInt();
        numLODs = in.getInt();
        numLODVertexes = new int[MAX_NUM_LODS];
        for (int lod = 0; lod < MAX_NUM_LODS; lod++){
            numLODVertexes[lod] = in.getInt();
        }
        numFixups = in.getInt();
        fixupTableStart = in.getInt();
        vertexDataStart = in.getInt();
        tangentDataStart = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
