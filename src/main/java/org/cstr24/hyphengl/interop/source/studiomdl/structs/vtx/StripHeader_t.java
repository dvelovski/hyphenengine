package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class StripHeader_t extends BaseStruct implements StructWrapper<StripHeader_t> {
    public static final int SIZE = 27;
    public int numIndices;
    public int indexOffset;

    public int numVerts;
    public int vertOffset;

    public short numBones;

    public short flags;

    public int numBoneStateChanges;
    public int boneStateChangeOffset;

    @Override
    public StripHeader_t parse(ByteBuffer in) {
        numIndices = in.getInt();
        indexOffset = in.getInt();

        numVerts = in.getInt();
        vertOffset = in.getInt();

        numBones = in.getShort();
        flags = uByteToShort(in.get());

        numBoneStateChanges = in.getInt();
        boneStateChangeOffset = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
