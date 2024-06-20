package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd;

import org.joml.Vector4f;

import java.util.ArrayList;

public class VVDFile {
    public vertexFileHeader_t header;
    public ArrayList<vertexFileFixup_t> fixups;
    public ArrayList<mstudiovertex_t> vertices;
    public ArrayList<Vector4f> tangents;
    public VVDFile(vertexFileHeader_t _header){
        this.header = _header;
        this.fixups = new ArrayList<>();
        this.vertices = new ArrayList<>();
        this.tangents = new ArrayList<>();
    }
}
