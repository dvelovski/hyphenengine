package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

public class SubMesh {
    public String partName = "";
    public boolean enabled = false;
    public boolean baseVertex = true;

    public int elementOffset; //this mesh part's starting index in the element array
    public int elementBase; //this amount is added to the values retrieved
    //i.e. if elements[elementOffset] = 0, GL uses 0 + elementBase
    public int elementCount;

    public int elementID;

    //public Material material OR materialinstance?
    //technically everything is an instance of a material, it shouldn't own the material itself
    public MaterialInstance materialInstance;

    public void enable(){
        this.enabled = true;
    }
    public void disable(){
        this.enabled = false;
    }
    public SubMesh elementBase(int _eBase){
        this.elementBase = _eBase;
        return this;
    }

    public static SubMesh create(String _name, int _idxOffset, int _idxCount){
        var part = new SubMesh();

        part.partName = _name;
        part.elementOffset = _idxOffset;
        part.elementCount = _idxCount;

        return part;
    }
}
