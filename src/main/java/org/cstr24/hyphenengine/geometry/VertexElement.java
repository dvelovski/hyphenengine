package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.data.ComponentType;
import org.cstr24.hyphenengine.data.ElementDescriptor;

public class VertexElement extends ElementDescriptor {
    public int relativeOffset;
    public boolean normalized;

    //public int bufferReference; //TODO multiple source buffers

    public VertexElement(ComponentType _type, int _componentCount) {
        this.componentType = _type;
        this.componentCount = _componentCount;
    }

    public VertexElement normalized() {
        normalized = true;
        return this;
    }

    public static VertexElement of(ComponentType _type, int _componentCount) {
        return new VertexElement(_type, _componentCount);
    }
}