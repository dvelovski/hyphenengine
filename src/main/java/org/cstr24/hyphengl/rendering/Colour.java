package org.cstr24.hyphengl.rendering;

public class Colour {
    public float r;
    public float g;
    public float b;
    public float a;

    public Colour(){
        this.r = this.g = this.b = 0.0f;
        this.a = 1.0f;
    }
    public Colour(float[] values){
        assert values.length == 4;
        this.r = values[0];
        this.g = values[1];
        this.b = values[2];
        this.a = values[3];
    }
    public Colour(float r, float g, float b){
        this(r, g, b, 1.0f);
    }
    public Colour(float r, float g, float b, float a){
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
