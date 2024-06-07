package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.interop.source.vtf.VTFLoader;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;

import java.io.IOException;
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
