package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class mstudiomodel_t extends BaseStruct implements StructWrapper<mstudiomodel_t> {
    public String name;

    public int type;

    public float boundingradius;

    public int nummeshes;
    public int meshindex;

    public int numvertices;
    public int vertexindex;
    public int tangentsindex;

    public int numattachments;
    public int attachmentindex;

    public int numeyeballs;
    public int eyeballindex;

    mstudio_modelvertexdata_t vertexdata;
    public ArrayList<mstudiomesh_t> meshes;

    @Override
    public mstudiomodel_t parse(ByteBuffer in) {
        name = SourceInterop.readNullTerminatedString(in, 64, true);

        //System.out.println("MODEL NAME: " + name);

        type = in.getInt();
        boundingradius = in.getFloat();

        nummeshes = in.getInt();
        meshindex = in.getInt();

        numvertices = in.getInt();
        vertexindex = in.getInt();
        tangentsindex = in.getInt();

        numattachments = in.getInt();
        attachmentindex = in.getInt();

        numeyeballs = in.getInt();
        eyeballindex = in.getInt();

        //vertexdata = new mstudio_modelvertexdata_t().parse(in);
        skip(in, Integer.BYTES * 2); //mstudio_modelvertexdata_t is made up of a few functions and the only data it carries is two void pointers - 4 bytes each (same as int). Skip 2 to cover this.
        skip(in, Integer.BYTES * 8); //int unused[8]

        meshes = new ArrayList<>();

        int preMeshPos = in.position();
        in.position(structPos + meshindex);

        for (int meshID = 0; meshID < nummeshes; meshID++){
            meshes.add(new mstudiomesh_t().parseStruct(in));
        }

        in.position(preMeshPos);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
