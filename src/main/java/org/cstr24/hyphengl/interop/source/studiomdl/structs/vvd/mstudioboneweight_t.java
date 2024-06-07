package org.cstr24.hyphengl.interop.source.studiomdl.structs.vvd;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

import static org.cstr24.hyphengl.interop.source.studiomdl.StudioModel.MAX_NUM_BONES_PER_VERT;

public class mstudioboneweight_t extends BaseStruct implements StructWrapper<mstudioboneweight_t> {
    public float[] weight;
    public char[] bone;
    public byte numbones;

    @Override
    public mstudioboneweight_t parse(ByteBuffer in) {
        weight = new float[MAX_NUM_BONES_PER_VERT];
        bone = new char[MAX_NUM_BONES_PER_VERT];

        for (int w = 0; w < MAX_NUM_BONES_PER_VERT; w++){
            weight[w] = in.getFloat();
        }
        for (int b = 0; b < MAX_NUM_BONES_PER_VERT; b++){
            bone[b] = (char) in.get();
        }

        numbones = in.get();

        return this;
    }

    @Override
    public int sizeOf() {
        return 16;
    }
}
