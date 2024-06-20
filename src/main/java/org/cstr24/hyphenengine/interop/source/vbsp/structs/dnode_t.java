package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class dnode_t extends BaseStruct implements StructWrapper<dnode_t> {
    public static final int SIZE = 32;

    public int planeNum; //index into plane array
    public int[] children = new int[2];

    public short[] mins = new short[3];
    public short[] maxs = new short[3];

    public int firstFace;
    public int numFaces;

    public short area;

    @Override
    public dnode_t parse(ByteBuffer in) {
        planeNum = in.getInt();

        children[0] = in.getInt();
        children[1] = in.getInt();

        mins[0] = in.getShort();
        mins[1] = in.getShort();
        mins[2] = in.getShort();

        maxs[0] = in.getShort();
        maxs[1] = in.getShort();
        maxs[2] = in.getShort();

        firstFace = uShortToInt(in.getShort());
        numFaces = uShortToInt(in.getShort());

        area = in.getShort();

        skip(in, 2); //pad to 32 bytes length
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
