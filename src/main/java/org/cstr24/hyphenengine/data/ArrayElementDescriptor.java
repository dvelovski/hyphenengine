package org.cstr24.hyphenengine.data;

public class ArrayElementDescriptor extends ElementDescriptor{
    public int length;

    public ArrayElementDescriptor() {
        super();
    }

    public ArrayElementDescriptor(ComponentType _type, int _len) {
        super(_type);
        this.length = _len;
    }

    public ArrayElementDescriptor(ComponentType _type, int _cCount, int _len) {
        super(_type, _cCount);
        this.length = _len;
    }

    @Override
    public ArrayElementDescriptor withType(ComponentType _type) {
        super.withType(_type);
        return this;
    }

    @Override
    public ArrayElementDescriptor withNumComponents(int _cCount) {
        super.withNumComponents(_cCount);
        return this;
    }

    public ArrayElementDescriptor withLength(int _len){
        this.length = _len;
        return this;
    }

    @Override
    public int computeSize() {
        return super.computeSize() * length;
    }

    public ArrayElementDescriptor copyFrom(ElementDescriptor other){
        this.componentCount = other.componentCount;
        this.componentType = other.componentType;

        this.type = Type.Array; //override 'other's' type - we're an array now, not a SingleVar for example

        if (other.isArray()){
            this.length = ((ArrayElementDescriptor) other).length;
        }

        return this;
    }
}
