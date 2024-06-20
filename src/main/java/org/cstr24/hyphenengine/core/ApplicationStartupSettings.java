package org.cstr24.hyphenengine.core;

import org.apache.commons.lang3.SystemUtils;
import org.cstr24.hyphenengine.backends.GraphicsBackend;
import org.cstr24.hyphenengine.backends.ogl.GLDSABackend;
import org.cstr24.hyphenengine.backends.ogl.GLNonDSABackend;

public class ApplicationStartupSettings {
    public GraphicsBackend backend;
    public boolean graphicsDebug = false;
    public boolean captureCursor = true;

    /**
     * Whether this application requires Source Engine interoperability.
     */
    public boolean requiresSourceInterop = false;

    public boolean requiresPhysicsLibrary = false;

    public OSWindowCreationInfo windowSpecifications = OSWindow.DEFAULT_WINDOW_PARAMETERS;

    public ApplicationStartupSettings(){
        backend = SystemUtils.IS_OS_MAC ? new GLNonDSABackend() : new GLDSABackend();
    }
}
