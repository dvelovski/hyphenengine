package org.cstr24.hyphenengine.backends.ogl;

import org.cstr24.hyphenengine.backends.GraphicsBackend;
import org.cstr24.hyphenengine.geometry.GLDSAMesh;
import org.cstr24.hyphenengine.geometry.IMeshFactory;
import org.cstr24.hyphenengine.rendering.shader.GLDSAShader;
import org.cstr24.hyphenengine.rendering.shader.IShaderFactory;
import org.cstr24.hyphenengine.textures.*;
import org.lwjgl.opengl.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL30.GL_CONTEXT_FLAGS;
import static org.lwjgl.opengl.GL43.*;

//OpenGL 4.6 backend.
public class GLDSABackend extends GraphicsBackend {
    private static final Logger LOGGER = Logger.getLogger(GLDSABackend.class.getName());

    IMeshFactory meshFactory = GLDSAMesh::new;
    ITextureFactory textureFactory = new ITextureFactory() {
        @Override
        public Texture2D createTexture2D(TextureData data) {
            return new GLDSATexture2D().fromInfo(data);
        }
        public Texture2D createTexture2D(){
            return new GLDSATexture2D();
        }
    };
    IShaderFactory shaderFactory = GLDSAShader::new;

    @Override
    public IMeshFactory getMeshFactory() {
        return this.meshFactory;
    }

    @Override
    public ITextureFactory getTextureFactory() {
        return textureFactory;
    }
    @Override
    public IShaderFactory getShaderFactory(){
        return shaderFactory;
    }

    @Override
    public boolean initialize() {
        LOGGER.log(Level.INFO, "OpenGL DSA Backend initialized.");
        return true;
    }

    @Override
    public void enableDebug() {
        debugEnabled = true;
    }

    @Override
    public void supplyGLFWContextHints() {
        glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        if (debugEnabled){
            glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
        }
        glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
    }
    @Override
    public void postWindowCreation(){
        GL.createCapabilities();

        if (debugEnabled){ //as we're running gl4.3+ we can assume debug output is available as long as we've created a debug context
            setupDebugPrint();
        }
    }
    public void setupDebugPrint(){
        int flagInt = glGetInteger(GL_CONTEXT_FLAGS);
        if ((flagInt & GL_CONTEXT_FLAG_DEBUG_BIT) != 0){
            glEnable(GL_DEBUG_OUTPUT);
            glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
            glDebugMessageCallback(GLDebugHandler::debugPrint, 0L);
            glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, true);
        }else{
            LOGGER.log(Level.WARNING, "Unable to setup debug messaging - it may not have been correctly configured in the GL context creation.");
        }
    }
}
