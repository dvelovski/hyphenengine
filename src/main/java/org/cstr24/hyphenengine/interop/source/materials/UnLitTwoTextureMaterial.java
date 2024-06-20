package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.interop.source.kv.KeyValueTree;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

public class UnLitTwoTextureMaterial extends HyMaterial implements SourceMaterial {
    public UnLitTwoTextureMaterial() {
        this.materialInstanceSupplier = MaterialInstance::new;

        this.materialName = "UnLitTwoTexture";

        this.createTextureProperty("baseTexture", 0);
        this.createTextureProperty("texture2", 1);
    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        var materialInstance = new MaterialInstance(this);

        loadTexture(vmtData, materialInstance, "$baseTexture", "baseTexture");
        loadTexture(vmtData, materialInstance, "$texture2", "texture2");

        return materialInstance;
    }

    @Override
    public void shaderUpdated() {

    }
}
