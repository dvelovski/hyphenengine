package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.vbsp.Constants;

import java.nio.ByteBuffer;

public class dface_t_v18 extends dface_t {
    public static final int SIZE = 72;

    public int[] averageLightColour = new int[Constants.MAX_LIGHTMAPS];

    @Override
    public dface_t parse(ByteBuffer in) {
        for (int i = 0; i < averageLightColour.length; i++) {
            averageLightColour[i] = in.getInt();
        }
        super.parse(in);
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
