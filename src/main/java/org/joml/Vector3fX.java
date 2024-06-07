package org.joml;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class Vector3fX extends Vector3f {
    public Vector3fX() {
    }

    public Vector3fX(float d) {
        super(d);
    }

    public Vector3fX(float x, float y, float z) {
        super(x, y, z);
    }

    public Vector3fX(Vector3fc v) {
        super(v);
    }

    public Vector3fX(Vector3ic v) {
        super(v);
    }

    public Vector3fX(Vector2fc v, float z) {
        super(v, z);
    }

    public Vector3fX(Vector2ic v, float z) {
        super(v, z);
    }

    public Vector3fX(float[] xyz) {
        super(xyz);
    }

    public Vector3fX(ByteBuffer buffer) {
        super(buffer);
    }

    public Vector3fX(int index, ByteBuffer buffer) {
        super(index, buffer);
    }

    public Vector3fX(FloatBuffer buffer) {
        super(buffer);
    }

    public Vector3fX(int index, FloatBuffer buffer) {
        super(index, buffer);
    }

    public float component(int component){
        switch (component){
            case 0 -> {
                return x;
            }
            case 1 -> {
                return y;
            }
            case 2 -> {
                return z;
            }
        }
        throw new IllegalArgumentException();
    }
}
