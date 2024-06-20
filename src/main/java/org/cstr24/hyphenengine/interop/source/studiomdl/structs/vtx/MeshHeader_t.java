package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MeshHeader_t extends BaseStruct implements StructWrapper<MeshHeader_t> {
    public static final int SIZE = 9;

    public static final int STRIPGROUP_IS_FLEXED = 0x01;
    public static final int STRIPGROUP_IS_HWSKINNED = 0x02;
    public static final int STRIPGROUP_IS_DELTA_FLEXED = 0x04;
    public static final int STRIPGROUP_SUPPRESS_HW_MORPH = 0x08;

    public int numStripGroups;
    public int stripGroupHeaderOffset;


    public short flags;
    public ArrayList<StripGroupHeader_t> stripGroupHeaders;

    @Override
    public MeshHeader_t parse(ByteBuffer in) {
        numStripGroups = in.getInt();
        stripGroupHeaderOffset = in.getInt();

        flags = uByteToShort(in.get());

        stripGroupHeaders = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
