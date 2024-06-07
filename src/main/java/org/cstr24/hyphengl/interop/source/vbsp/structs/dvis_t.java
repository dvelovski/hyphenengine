package org.cstr24.hyphengl.interop.source.vbsp.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.cstr24.hyphengl.interop.source.vbsp.Constants;

import java.nio.ByteBuffer;

public class dvis_t extends BaseStruct implements StructWrapper<dvis_t> {
    public int numClusters;
    public int[][] byteOffsets;

    @Override
    public dvis_t parse(ByteBuffer in) {
        numClusters = in.getInt();
        //System.out.println(">vis clusters: " + numClusters);
        byteOffsets = new int[numClusters][];
        for (int i = 0; i < numClusters; i++) {
            byteOffsets[i] = new int[2];
            byteOffsets[i][0] = in.getInt();
            byteOffsets[i][1] = in.getInt();
        }
        return this;
    }

    @Override
    public int sizeOf() {
        return Constants.BINARY_BASE_UNDEFINED;
    }
}
