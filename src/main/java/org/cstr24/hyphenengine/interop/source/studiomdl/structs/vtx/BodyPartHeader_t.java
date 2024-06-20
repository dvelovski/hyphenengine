package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class BodyPartHeader_t extends BaseStruct implements StructWrapper<BodyPartHeader_t> {
    public static final int SIZE = 8;

    public int numModels;
    public int modelOffset;

    public ArrayList<ModelHeader_t> models;

    @Override
    public BodyPartHeader_t parse(ByteBuffer in) {
        numModels = in.getInt();
        modelOffset = in.getInt();

        models = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
