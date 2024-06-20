package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public abstract class dleaf_t extends BaseStruct implements StructWrapper<dleaf_t> {
    public static final int SIZE = 32;

    public int contents;
    public short cluster;

    public short areaFlags;

    public short[] mins = new short[3];
    public short[] maxs = new short[3];

    //defined as unsigned short
    public int firstLeafFace;
    //defined as unsigned short
    public int numLeafFaces;
    //defined as unsigned short
    public int firstLeafBrush;
    //defined as unsigned short
    public int numLeafBrushes;

    public short leafWaterDataID;

    //version 17 BSP files have a modified structure that omits the ambient lighting data
    //the same shortened structure is used for v20 and presumably above

    @Override
    public dleaf_t parse(ByteBuffer in) {
        contents = in.getInt();
        cluster = in.getShort();

        areaFlags = in.getShort();

        mins[0] = in.getShort();
        mins[1] = in.getShort();
        mins[2] = in.getShort();

        maxs[0] = in.getShort();
        maxs[1] = in.getShort();
        maxs[2] = in.getShort();

        firstLeafFace = uShortToInt(in.getShort());
        numLeafFaces = uShortToInt(in.getShort());
        firstLeafBrush = uShortToInt(in.getShort());
        numLeafBrushes = uShortToInt(in.getShort());

        leafWaterDataID = in.getShort(); //-1 for not in water

        //2 bytes skipped for padding
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
