package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class mstudiobodyparts_t extends BaseStruct implements StructWrapper<mstudiobodyparts_t> {
    public String bodyPartName = "";
    public int nummodels;
    public int base;
    public int modelindex;

    public ArrayList<mstudiomodel_t> models;

    @Override
    public mstudiobodyparts_t parse(ByteBuffer in) {
        int sznameindex = in.getInt();
        nummodels = in.getInt();
        base = in.getInt();
        modelindex = in.getInt();

        bodyPartName = SourceInterop.fetchNullTerminatedString(in, structPos + sznameindex, 64);

        //System.out.println("BODY PART GROUP NAME: " + bodyPartName);

        models = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
