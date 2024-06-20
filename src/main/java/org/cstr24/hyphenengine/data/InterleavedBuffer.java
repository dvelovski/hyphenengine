package org.cstr24.hyphenengine.data;

import org.cstr24.hyphenengine.geometry.VertexLayout;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class InterleavedBuffer  {
    public ByteBuffer backingBuffer;
    public VertexLayout dataLayout;
    private int vertexSize;

    public InterleavedBuffer (VertexLayout layout, int vertexCount){
        //System.out.println("Layout size: " + layout.sizeOf());
        this.backingBuffer = BufferUtils.createByteBuffer(layout.sizeOf() * vertexCount);
        this.dataLayout = layout;
        this.vertexSize = layout.sizeOf();
    }
    public void interleaveVec2s(int element, ArrayList<Vector2f> data){
        int relativeOffset = dataLayout.getElementOffset(element);

        //System.out.println("vec2 relative offset " + relativeOffset);

        for (int i = 0; i < data.size(); i++) {
            Vector2f v2 = data.get(i);
            backingBuffer.position(relativeOffset + (vertexSize * i));
            backingBuffer.putFloat(v2.x).putFloat(v2.y);
        }
    }
    public void interleaveVec3s(int element, ArrayList<Vector3f> data){
        int relativeOffset = dataLayout.getElementOffset(element);

        //System.out.println("vec3 relative offset " + relativeOffset);

        for (int i = 0; i < data.size(); i++) {
            Vector3f v3 = data.get(i);
            backingBuffer.position(relativeOffset + (vertexSize * i));
            backingBuffer.putFloat(v3.x).putFloat(v3.y).putFloat(v3.z);
        }
    }
}
