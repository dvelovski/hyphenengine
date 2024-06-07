package org.cstr24.hyphengl.interop.source.vpk.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class VPKDirectoryEntry extends BaseStruct implements StructWrapper<VPKDirectoryEntry> {
    public static final int TERMINATOR = 0xFFFF;

    public String filename = "";
    public String extension = "";

    public int CRC; //unsigned int

    public int numPreloadBytes; //unsigned short
    public byte[] preloadedBytes;

    public int archiveIndex; //unsigned short

    public int entryOffset; //unsigned int
    public int entryLength;

    @Override
    public VPKDirectoryEntry parse(ByteBuffer in) {
        CRC = in.getInt();

        numPreloadBytes = uShortToInt(in.getShort());

        archiveIndex = uShortToInt(in.getShort());

        entryOffset = in.getInt();
        entryLength = in.getInt();

        int terminator = uShortToInt(in.getShort());

        if (numPreloadBytes > 0) {
            preloadedBytes = new byte[numPreloadBytes];
            in.get(preloadedBytes, 0, numPreloadBytes);
        }

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
