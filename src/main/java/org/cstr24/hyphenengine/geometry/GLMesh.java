package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.data.HyBaseBuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import static org.cstr24.hyphenengine.core.Engine.NO_ID;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.GL_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL41.GL_FIXED;

public abstract class GLMesh extends HyMesh {
    int sizeOfVertex;
    int subMeshCount;

    public int componentTypeToGLEnum(ComponentType in){
        switch (in){
            case HalfFloat -> {return GL_HALF_FLOAT;}

            case Float -> {
                return GL_FLOAT;
            }
            case Double -> {
                return GL_DOUBLE;
            }
            case Fixed -> {
                return GL_FIXED;
            }
            case Byte -> {
                return GL_BYTE;
            }
            case UnsignedByte -> {
                return GL_UNSIGNED_BYTE;
            }
            case Short -> {
                return GL_SHORT;
            }
            case UnsignedShort -> {
                return GL_UNSIGNED_SHORT;
            }
            case Int -> {
                return GL_INT;
            }
            case UnsignedInt -> {
                return GL_UNSIGNED_INT;
            }
            case Int_2_10_10_10_Rev -> {
                return GL_INT_2_10_10_10_REV;
            }
            case UnsignedInt_2_10_10_10_Rev -> {
                return GL_UNSIGNED_INT_2_10_10_10_REV;
            }
            case UnsignedInt_10F_11F_11F_REV -> {
                return GL_UNSIGNED_INT_10F_11F_11F_REV;
            }
        }
        return 0;
    }

    public GLMesh(){

    }

    @Override
    public abstract GLMesh apply();

    abstract void bindBuffer(int bufferID);

    @Override
    public void destroy() {
        GL30.glDeleteVertexArrays(meshID);
        destroyed = true;
        meshID = NO_ID;
    }

    @Override
    public GLMesh updateVertexData(HyBaseBuffer<?> data, int _vtxCount) {
        bindBuffer(data.getID());
        this.vertexCount = _vtxCount;
        return this;
    }

    @Override
    public abstract GLMesh updateElementBuffer(HyBaseBuffer<?> ebo, int _idxCount);

    @Override
    public GLMesh setElementIndexType(ComponentType newType) {
        if (newType.isUnsigned()){
            switch (newType){
                case UnsignedByte, UnsignedShort, UnsignedInt -> {
                    this.indexType = newType;
                }
                default -> {
                    throw new IllegalArgumentException("Type of indices for OpenGL mesh must be one of UnsignedByte, UnsignedShort, or UnsignedInt");
                }
            }
        }
        return this;
    }

    @Override
    public void bind() {
        GL45.glBindVertexArray(this.meshID);
    }

    @Override
    public int addSubmesh(SubMesh _sub) {
        //System.out.println(_sub.partName + " offset " + _sub.elementOffset + " num elems " + _sub.elementCount);
        subMeshes.add(_sub);
        subMeshCount++;

        _sub.elementID = subMeshCount;

        return subMeshCount;
    }

    @Override
    public SubMesh createSubmesh(String _name, int _idxStart, int _idxCount) {
        var newSubmesh = SubMesh.create(_name, _idxStart, _idxCount);
        addSubmesh(newSubmesh);
        return newSubmesh;
    }
}
