package org.cstr24.hyphenengine.core;

import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.graphics.Colours;
import org.cstr24.hyphenengine.rendering.Colour;

public final class OSWindowCreationInfo {
    private final int windowWidth;
    private final int windowHeight;
    private final String windowTitle;
    private final int monitorIndex;
    private final WindowMode windowMode;
    private Colour backgroundColour = Colours.AliceBlue;
    private HyFile windowIconHandle = null;

    public OSWindowCreationInfo(int windowWidth, int windowHeight, String windowTitle, int monitorIndex,
                                WindowMode windowMode) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowTitle = windowTitle;
        this.monitorIndex = monitorIndex;
        this.windowMode = windowMode;
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public int getRequestedMonitorIndex() {
        return monitorIndex;
    }

    public WindowMode getPreferredWindowMode() {
        return windowMode;
    }
    public Colour getBackgroundColour(){
        return backgroundColour;
    }
    public void setBackgroundColour(Colour newBg){
        this.backgroundColour = newBg;
    }

    public void setWindowIconHandle(HyFile handle){
        this.windowIconHandle = handle;
    }
    public HyFile getWindowIconHandle(){
        return windowIconHandle;
    }
}
