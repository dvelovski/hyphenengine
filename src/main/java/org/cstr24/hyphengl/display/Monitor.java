package org.cstr24.hyphengl.display;

public class Monitor {
    private long monitorPointer;

    public VideoMode[] videoModes;
    private VideoMode initialVideoMode;
    private VideoMode currentVideoMode;
    private String name = "";
    private float contentScaleX, contentScaleY;
    private int monitorPositionX, monitorPositionY;


    public Monitor(String _monitorName, long _monitorPointer){
        this.name = _monitorName;
        this.monitorPointer = _monitorPointer;
    }
    public Monitor setContentScale(float csx, float csy){
        this.contentScaleX = csx;
        this.contentScaleY = csy;
        return this;
    }
    public Monitor setPosition(int posX, int posY){
        this.monitorPositionX = posX;
        this.monitorPositionY = posY;
        return this;
    }

    //updates the video mode variable but does NOT change anything
    public void setInitialVideoMode(VideoMode vidMode){
        this.initialVideoMode = vidMode;
    }

    public VideoMode getInitialVideoMode() {
        return initialVideoMode;
    }

    public long getMonitorPointer() {
        return monitorPointer;
    }
}
