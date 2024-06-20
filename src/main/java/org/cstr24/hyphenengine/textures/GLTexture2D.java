package org.cstr24.hyphenengine.textures;

import org.cstr24.hyphenengine.backends.ogl.GLState;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL41;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;

public class GLTexture2D extends Texture2D implements GLTexture{
    private static final Logger LOGGER = Logger.getLogger(GLTexture2D.class.getName());

    public GLTexture2D(){
        this.textureID = GL41.glGenTextures();
    }


    @Override
    public void setAnisotropicFilteringLevel(AnisotropicFilteringLevel _level) {
        GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
        GL41.glTexParameterf(GL41.GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY_EXT, _level.value);
    }

    @Override
    public void bind(int slot) {
        GLState.activeTexture(slot);
        GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
    }

    @Override
    public boolean filterModePermitted(FilterMode _test) {
        return true;
    }

    @Override
    public void setMinificationMode(FilterMode minMode) {
        GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
        GL41.glTexParameteri(GL41.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filterModeToEnum(minMode));
    }

    @Override
    public void setMagnificationMode(FilterMode magMode) {
        switch (magMode){
            case Nearest, Linear -> {
                GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
                GL41.glTexParameteri(GL41.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filterModeToEnum(magMode));
            }
            default -> {
                LOGGER.log(Level.WARNING, magMode.name() + " not permitted as a texture magnification filter mode.");
            }
        }
    }

    @Override
    public void setWrapModes2D(WrapMode _sMode, WrapMode _tMode) {
        setWrapS(_sMode);
        setWrapT(_tMode);
    }

    @Override
    public void setWrapS(WrapMode modeS) {
        GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
        GL41.glTexParameteri(GL41.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, wrapModeToEnum(modeS));
    }

    @Override
    public void setWrapT(WrapMode modeT) {
        GLState.bindTexture(GL41.GL_TEXTURE_2D, textureID);
        GL41.glTexParameteri(GL41.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, wrapModeToEnum(modeT));
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public GLTexture2D fromInfo(TextureData data) {
        return this;
    }

    @Override
    public void unload() {
        GL41.glDeleteTextures(this.textureID);
    }
}
