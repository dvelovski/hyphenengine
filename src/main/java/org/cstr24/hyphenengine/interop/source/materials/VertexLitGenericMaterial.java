package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.interop.source.kv.KeyValueTree;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

public class VertexLitGenericMaterial extends HyMaterial implements SourceMaterial {

    public VertexLitGenericMaterial() {
        this.materialInstanceSupplier = MaterialInstance::new;

        this.materialName = "VertexLitGeneric";

        this.createTextureProperty("baseTexture", 0);
        this.createTextureProperty("lightwarpTexture", 1);
        /*var normalMapTexture = this.createTextureProperty("normalMap", 1);*/
    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        var materialInstance = new MaterialInstance(this);

        loadTexture(vmtData, materialInstance, "$basetexture", "baseTexture");
        loadTexture(vmtData, materialInstance, "$lightwarptexture", "lightwarpTexture");

        return materialInstance;
    }

    @Override
    public void shaderUpdated() {

    }
}
