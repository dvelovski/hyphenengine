package org.cstr24.hyphengl.interop.source.pak.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class ZIPEndOfCentralDirRecord extends BaseStruct implements StructWrapper<ZIPEndOfCentralDirRecord> {
    public int diskNum;

    public int centralDirectoryStartDisk;
    public int numCentralDirectoryRecordsOnDisk;
    public int totalCentralDirectoryRecords;

    public long centralDirectorySize;
    public long centralDirectoryOffset;

    public String comment;

    @Override
    public ZIPEndOfCentralDirRecord parse(ByteBuffer in) {
        int signature = in.getInt();

        diskNum = uShortToInt(in.getShort());

        centralDirectoryStartDisk = uShortToInt(in.getShort());
        numCentralDirectoryRecordsOnDisk = uShortToInt(in.getShort());
        totalCentralDirectoryRecords = uShortToInt(in.getShort());

        centralDirectorySize = uIntToLong(in.getInt());
        centralDirectoryOffset = uIntToLong(in.getInt());

        int commentLength = uShortToInt(in.getShort());
        if (commentLength != 0) {
            this.comment = nullTerminatedString(in, 1024);
        }

        return this;
    }

    @Override
    public int sizeOf() {
        return 20;
    }
}
