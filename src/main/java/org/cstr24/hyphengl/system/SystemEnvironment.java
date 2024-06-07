package org.cstr24.hyphengl.system;

import org.cstr24.hyphengl.display.Monitor;
import org.cstr24.hyphengl.display.VideoMode;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.glfw.GLFW.GLFW_CONNECTED;
import static org.lwjgl.glfw.GLFW.GLFW_DISCONNECTED;

public class SystemEnvironment {
    public static Monitor[] monitors;
    private static Monitor primaryMonitor;

    public static void initialize(){
        enumerateMonitors();
        enumerateGraphicsAdapters();

        GLFW.glfwSetMonitorCallback((monitor, event) -> {
            switch (event){
                case GLFW_CONNECTED -> {
                    System.out.println("Monitor was connected.");
                }
                case GLFW_DISCONNECTED -> {
                    System.out.println("Monitor was disconnected.");
                }
            }
        });
    }

    private static void enumerateMonitors(){
        try (MemoryStack stack = MemoryStack.stackPush()){
            PointerBuffer monitorPointers = GLFW.glfwGetMonitors();

            monitors = new Monitor[monitorPointers.capacity()];

            while (monitorPointers.hasRemaining()){
                long mPtr = monitorPointers.get();

                //System.out.println("*** monitor " + monitorPointers.position() + " ***");
                String monitorName = GLFW.glfwGetMonitorName(mPtr);
                //System.out.println("name: " + monitorName);

                var currMonitor = monitors[monitorPointers.position() - 1] = new Monitor(monitorName, mPtr);

                var fBuff1 = stack.mallocFloat(1);
                var fBuff2 = stack.mallocFloat(1);
                GLFW.glfwGetMonitorContentScale(mPtr, fBuff1, fBuff2);
                currMonitor.setContentScale(fBuff1.get(), fBuff2.get());

                var intBuff1 = stack.mallocInt(1);
                var intBuff2 = stack.mallocInt(1);
                GLFW.glfwGetMonitorPos(mPtr, intBuff1, intBuff2);
                currMonitor.setPosition(intBuff1.get(), intBuff2.get());

                if (mPtr == GLFW.glfwGetPrimaryMonitor()){
                    primaryMonitor = currMonitor;
                }

                GLFWVidMode.Buffer videoModes = GLFW.glfwGetVideoModes(mPtr);
                currMonitor.videoModes = new VideoMode[videoModes.capacity()];

                GLFWVidMode currentVideoMode = GLFW.glfwGetVideoMode(mPtr);

                while (videoModes.hasRemaining()){
                    GLFWVidMode vidMode = videoModes.get();

                    VideoMode videoMode = new VideoMode(vidMode.width(), vidMode.height(), vidMode.refreshRate());
                    currMonitor.videoModes[videoModes.position() - 1] = videoMode;

                    if (videoModesAreEqual(currentVideoMode, vidMode)){
                        currMonitor.setInitialVideoMode(videoMode);
                    }
                }

            }
        }
    }
    public static Monitor lookupMonitor(long handle){
        for (Monitor monitor : monitors) {
            if (monitor.getMonitorPointer() == handle){
                return monitor;
            }
        }
        return null;
    }

    public static Monitor getPrimaryMonitor(){
        return primaryMonitor;
    }



    private static void enumerateGraphicsAdapters(){

    }
    private static boolean videoModesAreEqual(GLFWVidMode mode1, GLFWVidMode mode2){
        return (mode1.width() == mode2.width()
         && mode1.height() == mode2.height()
         && mode1.refreshRate() == mode2.refreshRate()
         && mode1.redBits() == mode2.redBits()
         && mode1.greenBits() == mode2.greenBits()
         && mode1.blueBits() == mode2.blueBits());
    }
}
