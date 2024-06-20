package org.cstr24.hyphenengine.interop.source.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 3D vector class (source naming convention seems to be Vector2D, Vector, Vector4D, so any non-quantified 'Vector'
 * class is assumed to be a 3D one.
 */
public class vector_t extends BaseStruct implements StructWrapper<vector_t> {
    public static final int SIZE = 12;
    private static final int X = 0;
    private static final int Y = 1;
    private static final int Z = 2;

    public final float[] contents;

    public vector_t() {
        this.contents = new float[3];
    }

    public static vector_t initAllZero(){
        var x = new vector_t().zero();
        return x;
    }
    public vector_t zero(){
        return this.set(0, 0, 0);
    }

    @Override
    public vector_t parse(ByteBuffer in) {
        contents[X] = in.getFloat();
        contents[Y] = in.getFloat();
        contents[Z] = in.getFloat();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }

    public static vector_t create(ByteBuffer in) {
        return new vector_t().parse(in);
    }

    public float x(){
        return contents[X];
    }
    public float y(){
        return contents[Y];
    }
    public float z(){
        return contents[Z];
    }

    public vector_t swapSpace() {
        var temp = contents[Y];
        contents[Y] = contents[Z];
        contents[Z] = -temp;
        return this;
    }

    public vector_t set(float x, float y, float z){
        contents[X] = x;
        contents[Y] = y;
        contents[Z] = z;
        return this;
    }
    public vector_t set(vector_t other){
        contents[X] = other.contents[X];
        contents[Y] = other.contents[Y];
        contents[Z] = other.contents[Z];
        return this;
    }
    public float getComponent(int component){
        switch (component){
            case X -> {
                return contents[X];
            }
            case Y -> {
                return contents[Y];
            }
            case Z -> {
                return contents[Z];
            }
        }
        return -1;
    }
    public void setComponent(int component, float newValue){
        switch (component){
            case X -> contents[X] = newValue;
            case Y -> contents[Y] = newValue;
            case Z -> contents[Z] = newValue;
        }
    }
    public vector_t x(float newX){
        contents[X] = newX;
        return this;
    }
    public vector_t y(float newY){
        contents[Y] = newY;
        return this;
    }
    public vector_t z(float newZ){
        contents[Z] = newZ;
        return this;
    }

    public Vector3f toVec3f() {
        return new Vector3f(contents);
    }
    public float[] toFloatArray() {
        return Arrays.copyOf(contents, 3);
    }
}
