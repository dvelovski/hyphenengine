package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;

import java.util.function.Supplier;

public class LightmappedGenericMaterial extends HyMaterial implements SourceMaterial{

    public LightmappedGenericMaterial(){
        this.materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;
        this.materialName = "LightmappedGeneric";

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

    @Override
    public int compareInstances(MaterialInstance i1, MaterialInstance i2) {
        var bt1 = i1.getPropertyValue("baseTexture");
        var bt2 = i2.getPropertyValue("baseTexture");

        return Integer.compare((Integer) bt1, (Integer) bt2);
    }
}
