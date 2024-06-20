package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.entities.components.ModelComponent;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;

public class SubMeshState {
    public ModelComponent owner;
    public SubMesh part;
    public MaterialInstance materialInstance;
    public boolean enabled;

    public SubMeshState(SubMesh sMesh, ModelComponent oInstance) {
        this.part = sMesh;
        this.owner = oInstance;
        this.enabled = true; //visible by default.
    }
}
