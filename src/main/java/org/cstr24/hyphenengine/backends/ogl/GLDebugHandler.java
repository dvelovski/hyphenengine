package org.cstr24.hyphenengine.backends.ogl;

import org.lwjgl.opengl.GL45;
import org.lwjgl.opengl.GLDebugMessageCallback;

public class GLDebugHandler {
    public static void debugPrint(int source, int type, int id, int severity, int length, long ptrMessage, long userParam){
        if(id == 131169 || id == 131185 || id == 131218 || id == 131204) return;

        var strSource = "";
        var strType = "";
        var strSeverity = "";

        switch (source){
            case GL45.GL_DEBUG_SOURCE_API -> strSource = "API";
            case GL45.GL_DEBUG_SOURCE_WINDOW_SYSTEM -> strSource = "Window System";
            case GL45.GL_DEBUG_SOURCE_SHADER_COMPILER -> strSource = "Shader Compiler";
            case GL45.GL_DEBUG_SOURCE_THIRD_PARTY -> strSource = "Third Party";
            case GL45.GL_DEBUG_SOURCE_APPLICATION -> strSource = "Application";
            case GL45.GL_DEBUG_SOURCE_OTHER -> strSource = "Other";
            default -> strSource = "Unknown";
        }
        switch (type){
            case GL45.GL_DEBUG_TYPE_ERROR -> strType = "Error";
            case GL45.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR -> strType = "Deprecated Behaviour";
            case GL45.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR -> strType = "Undefined Behaviour";
            case GL45.GL_DEBUG_TYPE_PORTABILITY -> strType = "Portability";
            case GL45.GL_DEBUG_TYPE_PERFORMANCE -> strType = "Performance";
            case GL45.GL_DEBUG_TYPE_MARKER -> strType = "Marker";
            case GL45.GL_DEBUG_TYPE_OTHER -> strType = "Other";
        }
        switch (severity){
            case GL45.GL_DEBUG_SEVERITY_NOTIFICATION -> strSeverity = "Notification";
            case GL45.GL_DEBUG_SEVERITY_LOW -> strSeverity = "Low";
            case GL45.GL_DEBUG_SEVERITY_MEDIUM -> strSeverity = "Medium";
            case GL45.GL_DEBUG_SEVERITY_HIGH -> strSeverity = "High";
        }

        System.out.println("GL Error [" + strSeverity + "] " + strSource + ": " + strType + " message: " +
                GLDebugMessageCallback.getMessage(length, ptrMessage) + " {" + id + "}");
    }
}
