package org.cstr24.hyphengl.rendering.surfaces;

import org.cstr24.hyphengl.data.ComponentType;

public class MaterialTextureProperty extends MaterialProperty{
    public int slot; //which slot are we bound to?
    public MaterialTextureProperty(){
        this.componentType = ComponentType.Int;
    }
}
