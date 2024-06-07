package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;

import java.util.function.Supplier;

public class WaterMaterial extends HyMaterial implements SourceMaterial{
    public WaterMaterial(){
        this.materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;

        this.materialName = "Water";

    }

    @Override
    public void shaderUpdated() {

    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        return new MaterialInstance(this);
    }
}
