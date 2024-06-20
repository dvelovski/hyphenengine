package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.data.HyBaseBuffer;
import org.lwjgl.opengl.GL45;

public class GLDSAMesh extends GLMesh{

    public GLDSAMesh() {
        meshID = GL45.glCreateVertexArrays();
        indexType = ComponentType.UnsignedInt;
        created = true;
    }

    void bindBuffer(int bufferID){
        this.sizeOfVertex = vertexLayout.sizeOf();
        GL45.glVertexArrayVertexBuffer(meshID, 0, bufferID, 0, sizeOfVertex);
    }


    public GLMesh updateElementBuffer(HyBaseBuffer<?> ebo, int _idxCount) {
        GL45.glVertexArrayElementBuffer(meshID, ebo.getID());
        this.elementCount = _idxCount;
        return this;
    }

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
}
