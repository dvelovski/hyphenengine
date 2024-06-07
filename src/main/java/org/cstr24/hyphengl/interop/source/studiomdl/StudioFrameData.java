package org.cstr24.hyphengl.interop.source.studiomdl;

import org.cstr24.hyphengl.interop.source.structs.vector_t;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.Quaternion;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

public class StudioFrameData {
    private int index;

    public List<Matrix4f> boneMatrices;
    public StudioFrameData() {

    }
    public void setIndex(int i){
        this.index = i;
    }
    public int getIndex(){
        return this.index;
    }
}
