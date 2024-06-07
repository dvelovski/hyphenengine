package org.cstr24.hyphengl.data;

public enum ComponentType {
    HalfFloat(16), Float(32), Double(64), Fixed(16),

    Byte(8), UnsignedByte(8, true),
    Short(16), UnsignedShort(16, true),
    Int(32), UnsignedInt(32, true),

    Texture(32), LongTextureHandle(64),

    Int_2_10_10_10_Rev(32),
    UnsignedInt_2_10_10_10_Rev(32, true),
    UnsignedInt_10F_11F_11F_REV(32, true),

    Boolean(32);
    
    public final int SIZE;
    public final int BYTES;
    public final boolean unsigned;

    ComponentType(int size){
        this(size, false);
    }
    ComponentType(int size, boolean _unsigned){
        this.SIZE = size;
        this.BYTES = size / 8;
        this.unsigned = _unsigned;
    }
    public boolean isUnsigned(){
        return unsigned;
    }
}
