package org.cstr24.hyphengl.data;

import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.util.HashMap;

import static org.cstr24.hyphengl.engine.Engine.NO_ID;

public abstract class GL46BaseBuffer<T> extends HyBaseBuffer<T> {
    static final HashMap<BufferType, Integer> bufferTypeMappings = new HashMap<>();
    static final HashMap<DataUsageMode, Integer> dataUsageMappings = new HashMap<>();

    static {
        bufferTypeMappings.put(BufferType.ArrayBuffer, GL15.GL_ARRAY_BUFFER);
        bufferTypeMappings.put(BufferType.IndexBuffer, GL15.GL_ELEMENT_ARRAY_BUFFER);
        bufferTypeMappings.put(BufferType.PixelPackBuffer, GL21.GL_PIXEL_PACK_BUFFER);
        bufferTypeMappings.put(BufferType.PixelUnpackBuffer, GL21.GL_PIXEL_UNPACK_BUFFER);
        bufferTypeMappings.put(BufferType.UniformBuffer, GL31.GL_UNIFORM_BUFFER);

        dataUsageMappings.put(DataUsageMode.StaticDraw, GL15.GL_STATIC_DRAW);
        dataUsageMappings.put(DataUsageMode.StreamDraw, GL15.GL_STREAM_DRAW);
        dataUsageMappings.put(DataUsageMode.DynamicDraw, GL15.GL_DYNAMIC_DRAW);
    }

    GL46BaseBuffer(){
        genBuffers();
    }

    void genBuffers() {
        bufferID = GL45.glCreateBuffers();
        destroyed = false;
        created = true;
    }

    @Override
    public boolean destroy() {
        GL15.glDeleteBuffers(this.bufferID);
        destroyed = true;
        bufferID = NO_ID;
        return true;
    }

    boolean isValid(){
        return bufferID != NO_ID && !destroyed;
    }

    @Override
    public int getID() {
        return bufferID;
    }

    @Override
    public ByteBuffer map(MapMode mappingMode) {
        if (isValid()){
            return x_map(mappingMode, -1, -1);
        }
        return null;
    }

    @Override
    public ByteBuffer mapRanged(MapMode mappingMode, long start, long length) {
        if (isValid()){
            return x_map(mappingMode, start, length);
        }
        return null;
    }

    public ByteBuffer x_map(MapMode mappingMode, long start, long length){
        int accessMode;

        if (start != -1){
            switch (mappingMode){
                case ReadOnly -> accessMode = GL30.GL_MAP_READ_BIT;
                case WriteOnly -> accessMode = GL30.GL_MAP_WRITE_BIT;
                default -> accessMode = GL30.GL_MAP_READ_BIT & GL30.GL_MAP_WRITE_BIT;
            }
            return GL45.glMapNamedBufferRange(bufferID, start, length, accessMode);
        }else{
            switch (mappingMode){
                case ReadOnly -> accessMode = GL15.GL_READ_ONLY;
                case WriteOnly -> accessMode = GL15.GL_WRITE_ONLY;
                default -> accessMode = GL15.GL_READ_WRITE; //default is read-write
            }
            return GL45.glMapNamedBuffer(bufferID, accessMode);
        }
    }

    @Override
    public boolean unmap() {
        return GL45.glUnmapNamedBuffer(bufferID);
    }
}
