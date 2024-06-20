package org.cstr24.hyphenengine.backends;

import org.cstr24.hyphenengine.geometry.IMeshFactory;
import org.cstr24.hyphenengine.rendering.shader.IShaderFactory;
import org.cstr24.hyphenengine.textures.ITextureFactory;

public abstract class GraphicsBackend {
    public boolean debugEnabled = false;

    public abstract IMeshFactory getMeshFactory();
    public abstract ITextureFactory getTextureFactory();

    public abstract IShaderFactory getShaderFactory();

    public abstract boolean initialize();

    public abstract void enableDebug();

    public abstract void supplyGLFWContextHints();

    public abstract void postWindowCreation();
}
