package org.cstr24.hyphengl.geometry;

import org.cstr24.hyphengl.data.ComponentType;
import org.cstr24.hyphengl.data.HyBaseBuffer;

import java.util.ArrayList;

public abstract class HyMesh {
    public int meshID;

    public boolean created;
    public boolean destroyed;

    public HyBaseBuffer<?> vertexData;
    public HyBaseBuffer<?> elementBuffer;

    public VertexLayout vertexLayout;
    public ComponentType indexType; //indices can be unsigned short or unsigned int

    public ArrayList<SubMesh> subMeshes;

    HyMesh(){
        subMeshes = new ArrayList<>();
    }

    protected int vertexCount = 0;
    protected int elementCount = 0;

    public HyMesh setVertexLayout(VertexLayout layout){
        this.vertexLayout = layout;
        return this;
    }
    public HyMesh setVertexData(HyBaseBuffer<?> _vxData, int vertexCount){
        this.vertexData = _vxData;
        return updateVertexData(_vxData, vertexCount);
    }
    public HyMesh setElementBuffer(HyBaseBuffer<?> _idxData, ComponentType _elemType, int _elemCount){
        this.elementBuffer = _idxData;
        setElementIndexType(_elemType);
        return updateElementBuffer(_idxData, _elemCount);
    }

    public abstract HyMesh apply();
    public abstract void destroy();
    protected abstract HyMesh updateVertexData(HyBaseBuffer<?> data, int _vtxCount);
    protected abstract HyMesh updateElementBuffer(HyBaseBuffer<?> ebo, int _idxCount);
    public abstract HyMesh setElementIndexType(ComponentType newType);

    public int getVertexCount(){
        return vertexCount;
    }
    public int getElementCount(){
        return elementCount;
    }

    public abstract void bind();
    public abstract int addSubmesh(SubMesh _sub);
    public abstract SubMesh createSubmesh(String _name, int _idxStart, int _idxCount);
}
