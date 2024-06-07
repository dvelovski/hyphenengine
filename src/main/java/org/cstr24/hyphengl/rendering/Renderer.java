package org.cstr24.hyphengl.rendering;

import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.geometry.HyMesh;

public abstract class Renderer {
    private static Renderer theRenderer;
    public static Renderer get(){
        if (theRenderer == null) {
            theRenderer = new HyGLBasicRenderer();
        }
        return theRenderer;
    }

    public abstract void draw(HyMesh mesh);
}
