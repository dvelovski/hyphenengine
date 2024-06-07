package org.cstr24.hyphengl.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class FileHeader_t extends BaseStruct implements StructWrapper<FileHeader_t> {
    public int version;

    public int vertCacheSize;
    public int maxBonesPerStrip;
    public int maxBonesPerTri;
    public int maxBonesPerVert;

    public int checkSum;

    public int numLODs;

    public int materialReplacementListOffset;

    public int numBodyParts;
    public int bodyPartOffset;

    @Override
    public FileHeader_t parse(ByteBuffer in) {
        version = in.getInt();

        vertCacheSize = in.getInt();
        maxBonesPerStrip = uShortToInt(in.getShort());
        maxBonesPerTri = uShortToInt(in.getShort());
        maxBonesPerVert = in.getInt();

        checkSum = in.getInt();

        numLODs = in.getInt();

        materialReplacementListOffset = in.getInt();

        numBodyParts = in.getInt();
        bodyPartOffset = in.getInt();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
