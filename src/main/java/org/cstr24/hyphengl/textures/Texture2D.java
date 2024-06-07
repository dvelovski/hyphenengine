package org.cstr24.hyphengl.textures;

import org.cstr24.hyphengl.engine.Engine;

public abstract class Texture2D extends HyTexture{
    public int width;
    public int height;

    @Override
    public int getDepth() {
        return 1;
    }

    @Override
    public void setWrapR(WrapMode _rMode) { //NOP
    }
}
