package org.cstr24.hyphengl.geometry;

import org.cstr24.hyphengl.entities.components.ModelComponent;
import org.cstr24.hyphengl.rendering.surfaces.MaterialInstance;

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
