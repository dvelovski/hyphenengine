package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.data.HyBaseBuffer;
import org.lwjgl.opengl.GL41;
import org.lwjgl.opengl.GL45;

public class GLNonDSAMesh extends GLMesh{

    public GLNonDSAMesh(){
        meshID = GL41.glGenVertexArrays();
        indexType = ComponentType.UnsignedInt;
        created = true;
    }

    @Override
    public GLMesh apply() {
        if (vertexLayout != null){
            GL41.glBindVertexArray(meshID);

            for (int elem = 0; elem < vertexLayout.elements.size(); elem++) {
                GL41.glEnableVertexAttribArray(elem);

                var element = vertexLayout.elements.get(elem);
                int glEnumType = componentTypeToGLEnum(element.componentType);

                //System.out.println("element #" + e + " @ offset " + element.relativeOffset);
                switch (element.componentType){
                    case Double -> {
                    }
                    case Byte, UnsignedByte, Short, UnsignedShort, Int, UnsignedInt ->
                            GL41.glVertexAttribIPointer(elem, element.componentCount, glEnumType, element.relativeOffset, 0L);
                    default ->
                            GL41.glVertexAttribPointer(elem, element.componentCount, glEnumType, false, element.relativeOffset, 0L);
                }

                GL45.glVertexArrayAttribBinding(meshID, elem, 0);
            }
        }
        return this;
    }

    @Override
    void bindBuffer(int bufferID) {
        this.sizeOfVertex = vertexLayout.sizeOf();

    }

    @Override
    public GLMesh updateElementBuffer(HyBaseBuffer<?> ebo, int _idxCount) {
        return null;
    }
}
