package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;

public class BSPMaterial extends HyMaterial {
    public BSPMaterial(){
        this.materialName = "BSPSurface";

        var bTexture = this.createProperty("baseTexture", ComponentType.Int, 0);
        //bTexture.setTexturePropertyMetadata(bTexture, HyTextureTypes.TX_DIFFUSE);
        var bLightmap = this.createProperty("lightmapTexture", ComponentType.Int, 0);
        //bLightmap.setTexturePropertyMetadata(bTexture, HyTextureTypes.TX_LIGHTMAP);
        var bLightmapLayer = this.createProperty("lightmapTextureLayer", ComponentType.Int, 0);
    }

    @Override
    public void shaderUpdated() {
        //no-op for now, i just thought it would be useful to have
        //23-04-22 i can't remember why i thought it was useful. oh no
    }
}
