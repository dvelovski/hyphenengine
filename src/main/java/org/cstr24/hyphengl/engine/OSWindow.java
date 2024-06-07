package org.cstr24.hyphengl.engine;

import org.apache.commons.lang3.SystemUtils;
import org.cstr24.hyphengl.backends.GraphicsBackend;
import org.cstr24.hyphengl.display.Monitor;
import org.cstr24.hyphengl.display.VideoMode;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.input.*;
import org.cstr24.hyphengl.system.SystemEnvironment;
import org.cstr24.hyphengl.rendering.Colour;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class OSWindow {
    private static final Logger LOGGER = Logger.getLogger(OSWindow.class.getName());

    private long handle;
    private long contextHandle;

    private boolean fullscreen;
    private boolean created;
    private boolean destroyed;

    private boolean clearColourChanged = false;
    private Colour clearColour;

    private int positionX = 0;
    private int positionY = 0;

    private int currentWidth;
    private int currentHeight;

    private int framebufferWidth;
    private int framebufferHeight;

    private WindowMode displayMode;

    public OSWindow(){

    }
    public OSWindow(Application app){
        ApplicationStartupSettings startupSettings = app.startupSettings;
        OSWindowCreationInfo creationInfo = startupSettings.windowSpecifications;
        GraphicsBackend appBackend = startupSettings.backend;

        int effectiveWidth = creationInfo.getWindowWidth();
        int effectiveHeight = creationInfo.getWindowHeight();
        long windowTarget = 0L;

        Monitor targetMonitor = SystemEnvironment.monitors[creationInfo.getRequestedMonitorIndex()];
        VideoMode vidMode = targetMonitor.getInitialVideoMode();
        WindowMode winMode = creationInfo.getPreferredWindowMode();

        if (winMode == WindowMode.BorderlessFullscreen || winMode == WindowMode.Fullscreen) {
            effectiveWidth = vidMode.width();
            effectiveHeight = vidMode.height();
            windowTarget = targetMonitor.getMonitorPointer();
        }

        appBackend.supplyGLFWContextHints();

        handle = glfwCreateWindow(effectiveWidth, effectiveHeight, creationInfo.getWindowTitle(), windowTarget, 0L);
        currentWidth = effectiveWidth;
        currentHeight = effectiveHeight;
        displayMode = winMode;

        initCallbacks();
        makeCurrent();
        glfwSwapInterval(1);

        setWindowIcon(creationInfo.getWindowIconHandle());
        setClearColour(creationInfo.getBackgroundColour());

        appBackend.postWindowCreation();

        if (winMode == WindowMode.Windowed){
            centreWindow(targetMonitor);
        }
        if (startupSettings.captureCursor){
            captureCursor();
        }

        setRawMouseMotionCapture(true);

        created = true;
    }

    //TODO multiple icon resolution possibilities (change the parameter to an array)
    private void setWindowIcon(HyFile iconHandle){
        if (!SystemUtils.IS_OS_MAC && iconHandle != null){
            try {
                ByteBuffer iconBuffer = iconHandle.getFileAsByteBuffer();

                try (MemoryStack stack = MemoryStack.stackPush()){
                    GLFWImage.Buffer icon = GLFWImage.malloc(1, stack);
                    IntBuffer imgWidth = stack.mallocInt(1);
                    IntBuffer imgHeight = stack.mallocInt(1);
                    IntBuffer imgComponents = stack.mallocInt(1);

                    ByteBuffer imagePixels = STBImage.stbi_load_from_memory(iconBuffer, imgWidth, imgHeight, imgComponents, 4);
                    icon.position(0).width(imgWidth.get(0)).height(imgHeight.get(0)).pixels(imagePixels);

                    glfwSetWindowIcon(handle, icon);

                    STBImage.stbi_image_free(imagePixels);
                }
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Unable to load icon for window.", e);
            }
        }
    }

    private void initCallbacks(){
        glfwSetWindowCloseCallback(handle, winPtr -> {

        });
        glfwSetWindowSizeCallback(handle, (winPtr, width, height) -> {
            currentWidth = width;
            currentHeight = height;
            //System.out.println("new window size: " + width + " x " + height);
        });
        glfwSetFramebufferSizeCallback(handle, (winPtr, width, height) -> {
            framebufferWidth = width;
            framebufferHeight = height;
            //System.out.println("new framebuffer size: " + width + " x " + height);
        });
        glfwSetWindowPosCallback(handle, (winPtr, xpos, ypos) -> {
            positionX = xpos;
            positionY = ypos;
        });

        glfwSetKeyCallback(handle, (window, key, scancode, action, mods) -> {
            InputManager.get().queueEvent(
                    new KeyEvent(action, key).mods(mods)
            );
        });
        glfwSetCharModsCallback(handle, (window, codepoint, mods) -> {
            InputManager.get().queueEvent(
                    new CharEvent(codepoint).mods(mods)
            );
        });
        glfwSetCursorPosCallback(handle, (window, xpos, ypos) -> {
            InputManager.get().queueEvent(
                    new MouseEvent(InputEvent.Action.MouseMove, (int) xpos, (int) ypos)
            );
        });
    }

    public int getWidth(){
        return currentWidth;
    }
    public int getHeight(){
        return currentHeight;
    }
    public float getAspectRatio(){
        return (float) currentWidth / (float) currentHeight;
    }
    public int getFramebufferWidth(){
        return framebufferWidth;
    }
    public int getFramebufferHeight(){
        return framebufferHeight;
    }

    public void setSize(int width, int height){
        if (created && !destroyed){
            changeSize(width, height);
        }
    }
    private void changeSize(int width, int height){
        glfwSetWindowSize(handle, width, height);
        currentWidth = width;
        currentHeight = height;
    }

    public void setWindowMode(WindowMode newWindowMode){
        if (created && !destroyed){
            switchWindowDisplayMode(newWindowMode);
            if (newWindowMode == WindowMode.Windowed){
                centreWindow();
            }
        }
    }
    public void setWindowMode(int width, int height, WindowMode newWindowMode){
        if (created && !destroyed){
            switchWindowDisplayMode(newWindowMode);
            changeSize(width, height);

            if (newWindowMode == WindowMode.Windowed){
                centreWindow();
            }
        }
    }
    private void switchWindowDisplayMode(WindowMode newMode){
        long monitor = glfwGetWindowMonitor(this.handle);

        if (displayMode != newMode){
            switch (newMode){
                case Windowed -> {
                    glfwSetWindowMonitor(this.handle, 0L, positionX, positionY, currentWidth, currentHeight, GLFW_DONT_CARE);
                }
                case BorderlessFullscreen, Fullscreen -> {
                    glfwSetWindowMonitor(this.handle, monitor, 0, 0, currentWidth, currentHeight, GLFW_DONT_CARE);
                }
            }

            this.displayMode = newMode;
        }
    }
    public void centreWindow(){
        var monitorRef = (SystemEnvironment.getPrimaryMonitor());
        centreWindow(monitorRef);
    }

    public void centreWindow(Monitor mon){
        var initialVidMode = mon.getInitialVideoMode();

        if (initialVidMode != null){
            int posX = initialVidMode.width() / 2 - (this.currentWidth / 2);
            int posY = initialVidMode.height() / 2 - (this.currentHeight / 2);

            glfwSetWindowPos(handle, posX, posY);
        }
    }

    public void captureCursor(){
        glfwSetInputMode(this.handle, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
    }
    public void setRawMouseMotionCapture(boolean newStatus){
        if (glfwRawMouseMotionSupported()){
            glfwSetInputMode(this.handle, GLFW_RAW_MOUSE_MOTION,
                    (newStatus ? GLFW_TRUE : GLFW_FALSE));
            //System.out.println("raw mouse input: " + newStatus);
        }
    }
    public void releaseCursor(){
        glfwSetInputMode(this.handle, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void close(){
        glfwDestroyWindow(handle);
    }
    public boolean isCloseRequested(){
        return glfwWindowShouldClose(handle);
    }
    public void destroy(){
        glfwDestroyWindow(handle);
        destroyed = true;
    }
    public void makeCurrent(){
        glfwMakeContextCurrent(handle);
    }
    public void setClearColour(Colour newClearColour){
        this.clearColour = newClearColour;
        clearColourChanged = true;
    }

    //TODO the renderer should do this, not the window. And tbh, it should be wrapped in FrameBuffer abstractions, not touched directly.
    public void clear(){
        if (clearColourChanged){
            GL11.glClearColor(clearColour.r, clearColour.g, clearColour.b, 1f);
            clearColourChanged = false;
        }

        GL11.glViewport(0, 0, currentWidth, currentHeight);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
    }

    public void swapBuffers(){
        if (created && !destroyed){
            glfwSwapBuffers(handle);
        }
    }

    public void setTitle(String title){
        if (created && !destroyed){
            glfwSetWindowTitle(handle, title);
        }
    }

    public static final OSWindowCreationInfo DEFAULT_WINDOW_PARAMETERS = new OSWindowCreationInfo(1440, 900, "",0, WindowMode.Windowed);

    public long getWindowHandle() {
        return this.handle;
    }
}
