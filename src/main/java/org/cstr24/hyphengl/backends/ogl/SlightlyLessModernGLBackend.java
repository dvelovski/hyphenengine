package org.cstr24.hyphengl.backends.ogl;

import org.cstr24.hyphengl.backends.GraphicsBackend;
import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.geometry.IMeshFactory;
import org.cstr24.hyphengl.textures.GLTexture2D;
import org.cstr24.hyphengl.textures.HyTexture;
import org.cstr24.hyphengl.textures.ITextureFactory;
import org.cstr24.hyphengl.textures.TextureData;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.ARBDebugOutput;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL43;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_TRUE;
import static org.lwjgl.opengl.ARBDebugOutput.GL_DEBUG_OUTPUT_SYNCHRONOUS_ARB;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.GL_CONTEXT_FLAGS;
import static org.lwjgl.opengl.GL43.*;

//OpenGL 4.1 backend. So I can run it on MacOS. Eventually.
public class SlightlyLessModernGLBackend extends GraphicsBackend {

    ITextureFactory textureFactory = new ITextureFactory() {
        @Override
        public HyTexture createTexture2D(TextureData data) {
            return new GLTexture2D().fromInfo(data);
        }
    };

    @Override
    public IMeshFactory getMeshFactory() {
        return null;
    }

    @Override
    public ITextureFactory getTextureFactory() {
        return textureFactory;
    }

    @Override
    public boolean initialize() {
        return true;
    }

    @Override
    public void enableDebug() {
        debugEnabled = true;
    }

    @Override
    public void supplyGLFWContextHints() {
        GLFW.glfwWindowHint(GLFW_CLIENT_API, GLFW_OPENGL_API);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        GLFW.glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
        GLFW.glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        if (debugEnabled){
            GLFW.glfwWindowHint(GLFW_CONTEXT_DEBUG, GLFW_TRUE);
        }
        GLFW.glfwWindowHint(GLFW_SRGB_CAPABLE, GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW_DOUBLEBUFFER, GLFW_TRUE);
    }

    @Override
    public void postWindowCreation() {
        GL.createCapabilities();

        if (debugEnabled){
            //query for availability of debugging
            int flagInt = glGetInteger(GL_CONTEXT_FLAGS);
            if ((flagInt & GL_CONTEXT_FLAG_DEBUG_BIT) != 0){
                boolean debuggingAvailable = GL.getCapabilities().GL_KHR_debug;
                if (debuggingAvailable){
                    glEnable(GL_DEBUG_OUTPUT);
                    glEnable(GL_DEBUG_OUTPUT_SYNCHRONOUS);
                    glDebugMessageCallback(GLDebugHandler::debugPrint, 0L);
                    glDebugMessageControl(GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, GL_DONT_CARE, true);
                }
            }else{
                System.out.println("Unable to setup debug messaging - it may not have been correctly configured in the GL context creation.");
            }
        }
    }
}
