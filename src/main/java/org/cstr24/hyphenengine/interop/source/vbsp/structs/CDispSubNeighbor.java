package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class CDispSubNeighbor extends BaseStruct implements StructWrapper<CDispSubNeighbor> {
    public static final int SIZE = 6;
    /**
     * members are really 5 bytes long (short + 3 chars) but pad by 1 more byte)
     **/

    public int iNeighbor;   //this indexes into ddispinfos. initially 'unsigned short'.
    //0xFFFF if there is no neighbor here
    public char neighborOrientation; //(CCW) rotation of the neighbor wrt this displacement - wrt: with respect to?
    public char span; //where the neighbor fits onto this side of our displacement
    public char neighborSpan; //where we fit onto our neighbor

    @Override
    public CDispSubNeighbor parse(ByteBuffer in) {
        iNeighbor = uShortToInt(in.getShort());
        neighborOrientation = (char) in.get();
        span = (char) in.get();
        neighborSpan = (char) in.get();

        skip(in, 1);

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }


    public boolean isValid() {
        return iNeighbor != 0xFFFF;
    }
}
