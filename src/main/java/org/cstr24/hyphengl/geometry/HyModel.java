package org.cstr24.hyphengl.geometry;

import org.cstr24.hyphengl.assets.HyResource;

import java.util.ArrayList;

public class HyModel extends HyResource {
    public String modelName;
    public ArrayList<HyMesh> meshes;
    public ArrayList<Bone> hyBones;

    private int modelID;

    public HyModel(){
        meshes = new ArrayList<>();
    }

    //pointer to carry any extra required information
    public Object userData;

    //some thoughts on what this class needs to become
    //track instances
    //model.createInstance()?

    //what about... HyMesh can carry

    @Override
    public void unload() {
        meshes.forEach(HyMesh::destroy);
    }
    public int getBoneCount(){
        return hyBones.size();
    }
}
