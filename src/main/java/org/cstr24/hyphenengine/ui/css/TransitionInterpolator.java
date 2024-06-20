package org.cstr24.hyphenengine.ui.css;

public abstract class TransitionInterpolator<T> {
    public abstract T interpolate(Object start, Object end, double time);
    public double interpolateDouble(double a, double b, double time){
        return a * (1.0 - time) + (b * time);
    }
    public float interpolateFloat(float a, float b, double time){
        return (float) (a * (1.0 - time) + (b * time));
    }
}
