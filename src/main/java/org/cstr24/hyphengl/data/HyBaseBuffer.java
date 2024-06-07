package org.cstr24.hyphengl.data;

import org.cstr24.hyphengl.engine.Engine;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public abstract class HyBaseBuffer<T>{
    int bufferID = Engine.NO_ID;

    long capacity = 0;

    BufferType bufferType;
    DataUsageMode usageMode;

    boolean created = false;
    boolean destroyed = false;

    public abstract GL46ImmutableBuffer allocate(long length);
    public abstract boolean destroy();

    public abstract ByteBuffer map(MapMode mappingMode);
    public abstract ByteBuffer mapRanged(MapMode mappingMode, long start, long length);
    public abstract boolean unmap();

    public abstract int getID();

    public BufferType getBufferType(){
        return bufferType;
    }
    public DataUsageMode getUsageMode(){
        return usageMode;
    }

    public abstract T setData(ByteBuffer data);
    public abstract T setData(IntBuffer data);
    public abstract T setData(FloatBuffer data);
    public abstract T setData(short[] data);
    public abstract T setSubData(long byteOffset, ByteBuffer data);
    public abstract T setSubData(long byteOffset, IntBuffer data);
    public abstract T setSubData(long byteOffset, FloatBuffer data);
}
