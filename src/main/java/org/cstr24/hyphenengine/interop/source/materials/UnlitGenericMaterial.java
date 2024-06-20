package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.interop.source.kv.KeyValueTree;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

import java.util.function.Supplier;

public class UnlitGenericMaterial extends HyMaterial implements SourceMaterial{

    public UnlitGenericMaterial(){
        this.materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;

        this.materialName = "UnlitGeneric";

        var bTexture = this.createTextureProperty("baseTexture", 0);

    }

    @Override
    public void shaderUpdated() {

    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        var materialInstance = new MaterialInstance(this);
        loadTexture(vmtData, materialInstance, "$baseTexture", "baseTexture");

        return materialInstance;
    }
}
