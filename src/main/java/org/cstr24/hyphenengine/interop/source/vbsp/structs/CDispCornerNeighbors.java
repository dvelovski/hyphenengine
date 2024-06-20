package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.vbsp.Constants;

import java.nio.ByteBuffer;

public class CDispCornerNeighbors extends BaseStruct implements StructWrapper<CDispCornerNeighbors> {
    public static final int SIZE = 10;
    /**
     * members are really 9 bytes in size BUT we need to skip one as padding is inserted
     **/

    public int[] neighbors = new int[Constants.MAX_DISP_CORNER_NEIGHBORS];
    public short numNeighbors;

    @Override
    public CDispCornerNeighbors parse(ByteBuffer in) {
        neighbors[0] = uShortToInt(in.getShort());
        neighbors[1] = uShortToInt(in.getShort());
        neighbors[2] = uShortToInt(in.getShort());
        neighbors[3] = uShortToInt(in.getShort());

        numNeighbors = in.get();

        skip(in, 1); //bring us to 10 bytes in

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
