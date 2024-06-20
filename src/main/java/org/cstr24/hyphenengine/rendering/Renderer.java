package org.cstr24.hyphenengine.rendering;

import org.cstr24.hyphenengine.entities.HyEntity;
import org.cstr24.hyphenengine.geometry.HyMesh;
import org.cstr24.hyphenengine.geometry.HyModel;

public abstract class Renderer {
    public Renderer createRenderer(){
        return null;
    };

    public abstract void beginFrame();
    public abstract void endFrame();
    public abstract void draw(HyEntity entity);
    public abstract void draw(HyMesh mesh);
}
