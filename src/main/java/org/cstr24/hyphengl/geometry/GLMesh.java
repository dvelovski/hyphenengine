package org.cstr24.hyphengl.geometry;

import org.cstr24.hyphengl.data.ComponentType;
import org.cstr24.hyphengl.data.HyBaseBuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL45;

import static org.cstr24.hyphengl.engine.Engine.NO_ID;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL33.GL_INT_2_10_10_10_REV;
import static org.lwjgl.opengl.GL41.GL_FIXED;

public class GLMesh extends HyMesh {
    private int sizeOfVertex;
    private int subMeshCount;

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
        libCreate();
    }

    void libCreate(){
        meshID = GL45.glCreateVertexArrays();
        indexType = ComponentType.UnsignedInt;
        created = true;
    }

    @Override
    public GLMesh apply() {
        if (vertexLayout != null){
            for (int e = 0; e < vertexLayout.elements.size(); e++) {
                GL45.glEnableVertexArrayAttrib(meshID, e);

                var element = vertexLayout.elements.get(e);
                int glEnumType = componentTypeToGLEnum(element.componentType);

                //System.out.println("element #" + e + " @ offset " + element.relativeOffset);
                switch (element.componentType){
                    case Double -> {
                        //throw new YouAreRetardedException;
                    }
                    case Byte, UnsignedByte, Short, UnsignedShort, Int, UnsignedInt ->
                        GL45.glVertexArrayAttribIFormat(
                            meshID, e, element.componentCount, glEnumType, element.relativeOffset
                        );
                    default ->
                        GL45.glVertexArrayAttribFormat(
                            meshID, e, element.componentCount, glEnumType, false, element.relativeOffset
                        );
                }

                GL45.glVertexArrayAttribBinding(meshID, e, 0);
            }
        }
        return this;
    }

    private void bindBuffer(int bufferID){
        this.sizeOfVertex = vertexLayout.sizeOf();
        GL45.glVertexArrayVertexBuffer(meshID, 0, bufferID, 0, sizeOfVertex);
    }

    @Override
    public void destroy() {
        GL30.glDeleteVertexArrays(meshID);
        destroyed = true;
        meshID = NO_ID;
    }

    @Override
    protected GLMesh updateVertexData(HyBaseBuffer<?> data, int _vtxCount) {
        bindBuffer(data.getID());
        this.vertexCount = _vtxCount;
        return this;
    }

    @Override
    protected GLMesh updateElementBuffer(HyBaseBuffer<?> ebo, int _idxCount) {
        GL45.glVertexArrayElementBuffer(meshID, ebo.getID());
        this.elementCount = _idxCount;
        return this;
    }

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
