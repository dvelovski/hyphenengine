package org.cstr24.hyphenengine.interop.source.materials;

import org.cstr24.hyphenengine.assets.AssetLoader;
import org.cstr24.hyphenengine.interop.source.SourceAssetTypes;
import org.cstr24.hyphenengine.interop.source.kv.KeyValueTree;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

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
