package org.cstr24.hyphenengine.ui.datatypes;

public abstract class Stroke {
    public StrokeType type;
    public float strokeWidth;

    public Stroke(){

    }

    public abstract void discard();

    public abstract void apply(long ctx);

    public Stroke setStrokeWidth(float width){
        this.strokeWidth = width;
        return this;
    }
}
