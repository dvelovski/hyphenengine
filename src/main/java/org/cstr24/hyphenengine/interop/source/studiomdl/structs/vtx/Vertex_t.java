package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class Vertex_t extends BaseStruct implements StructWrapper<Vertex_t> {
    public short[] boneWeightIndex;
    public short numBones;

    public int origMeshVertID;

    public byte[] boneID;

    @Override
    public Vertex_t parse(ByteBuffer in) {
        boneWeightIndex = new short[3];
        for (int i = 0; i < 3; i++){
            boneWeightIndex[i] = uCharToShort(in.get());
        }
        numBones = uCharToShort(in.get());

        origMeshVertID = uShortToInt(in.getShort());

        boneID = new byte[3];
        for (int b = 0; b < 3; b++){
            boneID[b] = in.get();
        }

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
