package org.cstr24.hyphenengine.textures;

import org.cstr24.hyphenengine.assets.HyAsset;

public abstract class HyTexture extends HyAsset {
    protected int textureID;

    public ImageFormat imageFormat;
    public int mipCount;

    public Object userData;

    public int getTextureID(){
        return textureID;
    }

    public abstract void setAnisotropicFilteringLevel(AnisotropicFilteringLevel _level);

    public abstract void bind(int slot);

    public abstract boolean filterModePermitted(FilterMode _test);
    public void setFilterModes(FilterMode _minification, FilterMode _magnification){
        setMinificationMode(_minification);
        setMagnificationMode(_magnification);
    }
    public abstract void setMinificationMode(FilterMode _minification);
    public abstract void setMagnificationMode(FilterMode _magnification);
    public abstract void setWrapModes2D(WrapMode _sMode, WrapMode _tMode);
    public abstract void setWrapR(WrapMode rMode);
    public abstract void setWrapS(WrapMode _sMode);
    public abstract void setWrapT(WrapMode _tMode);

    public void setUserData(Object _uData){
        this.userData = _uData;
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract int getDepth();
    public int getMipmapCount(){
        return mipCount;
    }

    public abstract <T extends HyTexture> T fromInfo(TextureData data);
}
