package org.cstr24.hyphenengine.textures;

public interface ITextureFactory {
    public Texture2D createTexture2D(TextureData data);
    public Texture2D createTexture2D();
}
