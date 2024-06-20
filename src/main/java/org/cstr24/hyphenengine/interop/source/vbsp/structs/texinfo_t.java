package org.cstr24.hyphenengine.interop.source.vbsp.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class texinfo_t extends BaseStruct implements StructWrapper<texinfo_t> {
    public static int SIZE = 72;
    public float[][] textureVecs = new float[2][4];
    public float[][] lightmapVecs = new float[2][4];
    public int flags;
    public int texData;

    @Override
    public texinfo_t parse(ByteBuffer in) {
        textureVecs[0][0] = in.getFloat(); //S x component
        textureVecs[0][1] = in.getFloat(); //S y component
        textureVecs[0][2] = in.getFloat(); //S z component
        textureVecs[0][3] = in.getFloat(); //S offset component
        textureVecs[1][0] = in.getFloat(); //T x component
        textureVecs[1][1] = in.getFloat(); //T y component
        textureVecs[1][2] = in.getFloat(); //T z component
        textureVecs[1][3] = in.getFloat(); //T offset component

        lightmapVecs[0][0] = in.getFloat(); //S x component
        lightmapVecs[0][1] = in.getFloat(); //S y component
        lightmapVecs[0][2] = in.getFloat(); //S z component
        lightmapVecs[0][3] = in.getFloat(); //S offset component
        lightmapVecs[1][0] = in.getFloat(); //T x component
        lightmapVecs[1][1] = in.getFloat(); //T y component
        lightmapVecs[1][2] = in.getFloat(); //T z component
        lightmapVecs[1][3] = in.getFloat(); //T offset component

        flags = in.getInt();
        texData = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
