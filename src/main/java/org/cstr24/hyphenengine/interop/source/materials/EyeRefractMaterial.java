package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.interop.source.kv.KeyValueTree;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

import java.util.function.Supplier;

public class EyeRefractMaterial extends HyMaterial implements SourceMaterial {
    public EyeRefractMaterial() {
        this.materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;

        this.materialName = "EyeRefract";

        this.createTextureProperty("iris", 0);
        this.createTextureProperty("corneaTexture", 1);
        this.createTextureProperty("lightwarptexture", 1);
    }

    @Override
    public MaterialInstance parseInstance(KeyValueTree vmtData) {
        var materialInstance = new MaterialInstance(this);

        loadTexture(vmtData, materialInstance, "$iris", "iris");
        loadTexture(vmtData, materialInstance, "$corneaTexture", "corneaTexture");
        loadTexture(vmtData, materialInstance, "$lightwarpTexture", "lightwarpTexture");

        return materialInstance;
    }

    @Override
    public void shaderUpdated() {

    }
}
