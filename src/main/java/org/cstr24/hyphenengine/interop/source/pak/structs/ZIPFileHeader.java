package org.cstr24.hyphenengine.interop.source.pak.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class ZIPFileHeader extends BaseStruct implements StructWrapper<ZIPFileHeader> {
    public int versionRequiredForExtraction;

    public int bitFlag;
    public int compressionMethod;

    public int lastModificationDate;
    public int lastModificationTime;

    public long uncompressedCRC32;

    public long compressedSize;
    public long uncompressedSize;

    public String fileName;
    public byte[] fileExtraFieldData;

    public long fileDataOffset;

    @Override
    public ZIPFileHeader parse(ByteBuffer in) {
        skip(in, 4); //skip the header

        versionRequiredForExtraction = uShortToInt(in.getShort());

        bitFlag = uShortToInt(in.getShort());
        compressionMethod = uShortToInt(in.getShort());

        lastModificationTime = uShortToInt(in.getShort());
        lastModificationDate = uShortToInt(in.getShort());

        uncompressedCRC32 = uIntToLong(in.getInt());

        compressedSize = uIntToLong(in.getInt());
        uncompressedSize = uIntToLong(in.getInt());

        int filenameLength = uShortToInt(in.getShort());
        int extraFieldLength = uShortToInt(in.getShort());

        if (filenameLength > 0) {
            var filenameBytes = new byte[filenameLength];
            in.get(filenameBytes);
            fileName = new String(filenameBytes);
        }

        if (extraFieldLength > 0) {
            fileExtraFieldData = new byte[extraFieldLength];
            in.get(fileExtraFieldData);
        }

        fileDataOffset = in.position();

        skip(in, (int) compressedSize);

        return this;
    }

    @Override
    public int sizeOf() {
        return 30;
    }
}
