package org.cstr24.hyphengl.interop.source.materials;

import org.cstr24.hyphengl.assets.AssetLoader;
import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.SourceAssetTypes;
import org.cstr24.hyphengl.interop.source.kv.KeyValueTree;
import org.cstr24.hyphengl.interop.source.vtf.VTFLoader;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;
import org.cstr24.hyphengl.textures.HyTexture;

import java.io.IOException;

public interface SourceMaterial {
    MaterialInstance parseInstance(KeyValueTree vmtData);
    default void loadTexture(KeyValueTree vmtData, MaterialInstance inst, String vmtKey, String targetProperty){
        String texPath = "";

        if (vmtData.treeRoot.hasChild(vmtKey)){
            texPath = vmtData.treeRoot.getChild(vmtKey).getValue().toLowerCase();
        }

        var texHandle = AssetLoader.get().loadResource(
            SourceAssetTypes.VTF, "materials/" + texPath + (texPath.endsWith(".vtf") ? "" : ".vtf")
        );

        inst.setProperty(targetProperty, texHandle);
    }
}
