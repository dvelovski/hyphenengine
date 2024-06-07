package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ModelHeader_t extends BaseStruct implements StructWrapper<ModelHeader_t> {
    public static final int SIZE = 8;

    public int numLODs;
    public int lodOffset;
    public ArrayList<ModelLODHeader_t> modelLODs;


    @Override
    public ModelHeader_t parse(ByteBuffer in) {
        numLODs = in.getInt();
        lodOffset = in.getInt();

        modelLODs = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
