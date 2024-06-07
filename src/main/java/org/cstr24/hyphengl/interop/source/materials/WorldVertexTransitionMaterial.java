package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.assets.AssetLoader;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.SourceAssetTypes;
import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.interop.source.vtf.VTFLoader;
import org.cstr24.hyphengl.rendering.surfaces.HyMaterial;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;

import java.io.IOException;
import java.util.function.Supplier;

public class WorldVertexTransitionMaterial extends HyMaterial implements SourceMaterial{

    public WorldVertexTransitionMaterial(){
        this.materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;

        this.materialName = "WorldVertexTransition";

        var bTexture = this.createTextureProperty("baseTexture", 0);
        var bTexture2 = this.createTextureProperty("baseTexture2", 1);
    }

    @Override
    public void shaderUpdated() {

    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        var materialInstance = new MaterialInstance(this);

        loadTexture(vmtData, materialInstance, "$baseTexture", "baseTexture");
        loadTexture(vmtData, materialInstance, "$baseTexture2", "baseTexture2");

        return materialInstance;
    }
}
