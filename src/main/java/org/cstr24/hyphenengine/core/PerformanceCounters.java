package org.cstr24.hyphenengine.core;

public class PerformanceCounters {

    private double updateBeginTime;
    private double updateEndTime;
    private double lastUpdateDuration;

    private double frameBeginTime;
    private double frameEndTime;
    private double lastFrameDuration;
    private int fps;
    private int frameCounter;
    private double lastFPSUpdate;

    public void updateBegin(double time){
        updateBeginTime = time;
    }
    public void updateEnd(double time){
        updateEndTime = time;
        lastUpdateDuration = updateEndTime - updateBeginTime;
    }
    public double getLastUpdateTime(){
        return lastUpdateDuration;
    }

    public void renderBegin(double time){
        frameBeginTime = time;
    }
    public void renderEnd(double time){
        frameEndTime = time;
        lastFrameDuration = frameEndTime - frameBeginTime;

        frameCounter++;

        if (time - lastFPSUpdate > 1.0d){
            fps = frameCounter;
            frameCounter = 0;
            lastFPSUpdate = time;
        }
    }
    public double getLastFrameTime(){
        return lastFrameDuration;
    }
    public int getFPS(){
        return fps;
    }
}
