package org.cstr24.hyphengl.backends;

import org.cstr24.hyphengl.geometry.IMeshFactory;
import org.cstr24.hyphengl.textures.ITextureFactory;

public abstract class GraphicsBackend {
    public boolean debugEnabled = false;

    public abstract IMeshFactory getMeshFactory();
    public abstract ITextureFactory getTextureFactory();
    public abstract boolean initialize();

    public abstract void enableDebug();

    public abstract void supplyGLFWContextHints();

    public abstract void postWindowCreation();
}
