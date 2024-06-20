package org.cstr24.hyphenengine.data;

public class ElementDescriptor {
    public ComponentType componentType;
    public int componentCount; //the amount of individual dataTypes that this represents
    //i.e. 3 floats = dataType.FLOAT and dataType of 3
    //for a texture - componentCount 1 = 1D texture. componentCount 2 = 2D texture. componentCount 3 = 3D. componentCount 6 = cube.
    public Type type = Type.SingleVar;

    public ElementDescriptor(){

    }

    public ElementDescriptor(ComponentType _type){
        this.componentType = _type;
        this.componentCount = 1;
    }

    public ElementDescriptor(ComponentType _type, int _cCount) {
        this.componentType = _type;
        this.componentCount = _cCount;
    }

    public ElementDescriptor withType(ComponentType _type) {
        this.componentType = _type;
        return this;
    }

    public ElementDescriptor withNumComponents(int _cCount) {
        this.componentCount = _cCount;
        return this;
    }

    public int computeSize() {
        return componentCount * componentType.BYTES;
    }

    public ElementDescriptor ofVector(int _cCount){
        this.componentCount = _cCount;
        this.type = Type.Vector;
        return this;
    }

    public ElementDescriptor of2x2Matrix(){
        this.componentCount = 4;
        this.type = Type.Matrix;
        return this;
    }
    public ElementDescriptor of3x3Matrix(){
        this.componentCount = 9;
        this.type = Type.Matrix;
        return this;
    }
    public ElementDescriptor of4x4Matrix(){
        this.componentCount = 16;
        this.type = Type.Matrix;
        return this;
    }
    public ElementDescriptor ofTexture(int components){
        this.type = Type.Texture;
        this.componentCount = components;
        this.componentType = ComponentType.Int;
        return this;
    }

    public static ElementDescriptor create(ComponentType _type, int _cCount) {
        return new ElementDescriptor(_type, _cCount);
    }

    @Override
    public String toString() {
        return "{" +
                "componentType=" + componentType +
                ", componentCount=" + componentCount +
                ", type=" + type +
                '}';
    }

    public boolean isArray(){
        return this.type == Type.Array;
    }

    public enum Type{
        SingleVar, Array, ArrayOfArrays, Vector, Matrix, Texture;
    }
}
