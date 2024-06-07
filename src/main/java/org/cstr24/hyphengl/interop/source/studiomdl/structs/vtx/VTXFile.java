package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import java.util.ArrayList;

public class VTXFile {
    public FileHeader_t header;
    public ArrayList<BodyPartHeader_t> bodyParts;

    public VTXFile(FileHeader_t _header){
        this.header = _header;
        this.bodyParts = new ArrayList<>();
    }
}
