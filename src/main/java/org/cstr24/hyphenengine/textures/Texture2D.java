package org.cstr24.hyphenengine.textures;

import org.cstr24.hyphenengine.core.Engine;

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

    public static Texture2D create(){
        return Engine.getBackend().getTextureFactory().createTexture2D();
    }
}
