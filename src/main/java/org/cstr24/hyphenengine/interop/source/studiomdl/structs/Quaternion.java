package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;

public class Quaternion extends BaseStruct implements StructWrapper<Quaternion> {
    public static final int SIZE = 16;
    public float x;
    public float y;
    public float z;
    public float w;

    public static Quaternion initAllZero(){
        return new Quaternion().zero();
    }
    public Quaternion zero(){
        return this.set(0, 0, 0, 0);
    }

    @Override
    public Quaternion parse(ByteBuffer in) {
        x = in.getFloat();
        y = in.getFloat();
        z = in.getFloat();
        w = in.getFloat();

        return this;
    }

    public Quaternion set(float x, float y, float z, float w){
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }
    public Quaternion set(Quaternion other){
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }
    public void setComponent(int component, float value){
        switch (component){
            case 0 -> this.x = value;
            case 1 -> this.y = value;
            case 2 -> this.z = value;
            case 3 -> this.w = value;
        }
    }
    public float component(int component){
        switch (component){
            case 0 -> {
                return this.x;
            }
            case 1 -> {
                return this.y;
            }
            case 2 -> {
                return this.z;
            }
            case 3 -> {
                return this.w;
            }
            default -> {
                return -1;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Quaternion other){
            return (this.x == other.x) && (this.y == other.y) && (this.z == other.z) && (this.w == other.w);
        }else{
            return false;
        }
    }

    public Quaternionf toQuaternionf(){
        return new Quaternionf(x, y, z, w);
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
