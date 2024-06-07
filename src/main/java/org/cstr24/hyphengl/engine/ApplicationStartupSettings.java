package org.cstr24.hyphengl.engine;

import org.apache.commons.lang3.SystemUtils;
import org.cstr24.hyphengl.backends.GraphicsBackend;
import org.cstr24.hyphengl.backends.ogl.ModernGLBackend;
import org.cstr24.hyphengl.backends.ogl.SlightlyLessModernGLBackend;

public class ApplicationStartupSettings {
    public GraphicsBackend backend;
    public boolean graphicsDebug = false;
    public boolean captureCursor = true;

    /**
     * Whether this application requires Source Engine interoperability.
     */
    public boolean requiresSourceInterop = false;

    public OSWindowCreationInfo windowSpecifications = OSWindow.DEFAULT_WINDOW_PARAMETERS;

    public ApplicationStartupSettings(){
        backend = SystemUtils.IS_OS_MAC ? new SlightlyLessModernGLBackend() : new ModernGLBackend();
    }
}
