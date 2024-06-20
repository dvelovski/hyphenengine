package org.cstr24.hyphenengine.textures;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT;

public class GLDSATexture2D extends Texture2D implements GLTexture{
    private static final Logger LOGGER = Logger.getLogger(GLDSATexture2D.class.getName());

    public GLDSATexture2D(){
        this.textureID = GL45.glCreateTextures(GL11.GL_TEXTURE_2D);
    }

    public Texture2D fromInfo(TextureData creationInfo){
        int iFormatToUse = creationInfo.internalFormat;
        if (creationInfo.format.compressed){
            iFormatToUse = imageFormatToEnum(creationInfo.format);
        }
        this.width = creationInfo.imageWidth;
        this.height = creationInfo.imageHeight;
        this.mipCount = creationInfo.mipData[0].length;

        GL45.glTextureStorage2D(
            this.textureID,
            creationInfo.mipData[0].length,
            iFormatToUse,
            creationInfo.imageWidth,
            creationInfo.imageHeight
        );
        //System.out.println("--- uploading: " + creationInfo.format + " | " + imageFormatToEnum(creationInfo.format) + " | internal format: " + creationInfo.internalFormat);


        for (int i = 0; i < creationInfo.mipData[0].length; i++){
            var mip = creationInfo.mipData[0][i];
            if (creationInfo.format.compressed){
                GL45.glCompressedTextureSubImage2D(
                    this.textureID,
                    mip.level,
                    0,
                    0,
                    mip.width,
                    mip.height,
                    imageFormatToEnum(creationInfo.format),
                    mip.data
                );
            }else{

            }
        }
        return this;
    }

    @Override
    public void setAnisotropicFilteringLevel(AnisotropicFilteringLevel _level) {
        GL45.glTextureParameterf(this.textureID, GL_TEXTURE_MAX_ANISOTROPY_EXT, _level.value);
    }

    @Override
    public void bind(int slot) {
        GL45.glBindTextureUnit(slot, this.textureID);
    }

    @Override
    public boolean filterModePermitted(FilterMode _test) {
        return true; //TODO this function might not be needed
    }

    @Override
    public void setMinificationMode(FilterMode minMode) {
        GL45.glTextureParameteri(
            this.textureID,
            GL11.GL_TEXTURE_MIN_FILTER,
            filterModeToEnum(minMode)
        );
    }

    @Override
    public void setMagnificationMode(FilterMode magMode) {

        switch (magMode){
            case Linear, Nearest ->
                GL45.glTextureParameteri(
                    this.textureID,
                    GL11.GL_TEXTURE_MAG_FILTER,
                    filterModeToEnum(magMode)
                );
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
    public void setWrapS(WrapMode _sMode) {
        GL45.glTextureParameteri(
            textureID,
            GL11.GL_TEXTURE_WRAP_S,
            wrapModeToEnum(_sMode)
        );
    }

    @Override
    public void setWrapT(WrapMode _tMode) {
        GL45.glTextureParameteri(
            textureID,
            GL11.GL_TEXTURE_WRAP_T,
            wrapModeToEnum(_tMode)
        );
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
    public void unload() {
        //System.out.println("deleting texture " + textureID);
        GL45.glDeleteTextures(textureID);
    }
}
