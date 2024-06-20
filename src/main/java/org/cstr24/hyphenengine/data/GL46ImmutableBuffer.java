package org.cstr24.hyphenengine.data;

import org.lwjgl.opengl.GL45;

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class GL46ImmutableBuffer extends GL46BaseBuffer<GL46ImmutableBuffer> {
    //TODO generify
    private int defaultMappingFlags = GL45.GL_DYNAMIC_STORAGE_BIT | GL45.GL_MAP_READ_BIT | GL45.GL_MAP_WRITE_BIT;


    public GL46ImmutableBuffer() {

    }

    public void ensureCompliance(){
        if (capacity > 0){
            //delete, re-create since we're using BufferStorage
            //System.out.println("must recreate for immutable storage");
            destroy();
            genBuffers();
        }
    }

    @Override
    public GL46ImmutableBuffer allocate(long length) {
        if (bufferID != 0){
            ensureCompliance();

            GL45.glNamedBufferStorage(bufferID, length, defaultMappingFlags);

            this.capacity = length;
        }
        return this;
    }

    @Override
    public GL46ImmutableBuffer setData(ByteBuffer data) {
        data.rewind();
        if (bufferID != 0){
            ensureCompliance();
            GL45.glNamedBufferStorage(bufferID, data, defaultMappingFlags);

            this.capacity = data.limit();
        }
        return this;
    }

    @Override
    public GL46ImmutableBuffer setData(IntBuffer data){
        data.rewind();

        if (bufferID != 0){
            //System.out.println("IB set data 1 " + GL30.glGetError());
            ensureCompliance();
            GL45.glNamedBufferStorage(bufferID, data, defaultMappingFlags);
            //System.out.println("IB set data 2 " + GL30.glGetError());

            this.capacity = data.limit();
        }
        return this;
    }
    @Override
    public GL46ImmutableBuffer setData(FloatBuffer data){
        data.rewind();

        if (bufferID != 0){
            //System.out.println("FB set data 1 " + GL30.glGetError());

            ensureCompliance();
            GL45.glNamedBufferStorage(bufferID, data, defaultMappingFlags);
            //System.out.println("FB set data 2 " + GL30.glGetError());

            this.capacity = data.limit();
        }
        return this;
    }

    @Override
    public GL46ImmutableBuffer setData(short[] data) {
        if (bufferID != 0){
            ensureCompliance();
            GL45.glNamedBufferStorage(bufferID, data, defaultMappingFlags);
            this.capacity = data.length;
        }
        return this;
    }

    @Override
    public GL46ImmutableBuffer setSubData(long byteOffset, ByteBuffer data) {
        long dataSize = data.limit();
        if (byteOffset + dataSize <= capacity){
            GL45.glNamedBufferSubData(bufferID, byteOffset, data);
        }else{
            throw new BufferOverflowException();
        }
        return this;
    }

    @Override
    public GL46ImmutableBuffer setSubData(long byteOffset, IntBuffer data){
        long dataSize = (long) data.limit() * Integer.BYTES;
        if (byteOffset + dataSize <= capacity){
            GL45.glNamedBufferSubData(bufferID, byteOffset, data);
        }else{
            throw new BufferOverflowException();
        }
        return this;
    }
    @Override
    public GL46ImmutableBuffer setSubData(long byteOffset, FloatBuffer data){
        long dataSize = (long) data.limit() * Float.BYTES;
        if (byteOffset + dataSize <= capacity){
            GL45.glNamedBufferSubData(bufferID, byteOffset, data);
        }else{
            throw new BufferOverflowException();
        }
        return this;
    }
}
