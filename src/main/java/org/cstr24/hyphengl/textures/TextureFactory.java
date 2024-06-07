package org.cstr24.hyphengl.textures;

public abstract class TextureFactory {
    private static ITextureFactory instance;
    public static void setInstance(ITextureFactory factory){
        instance = factory;
    }

    public static HyTexture createTex2D(TextureData data){
        return instance.createTexture2D(data);
    }
}
