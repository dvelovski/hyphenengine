package org.cstr24.hyphengl.interop.source.pak;

import org.cstr24.hyphengl.filesystem.FilesystemEntry;

public class PAKFileSystemEntry extends FilesystemEntry {
    public int compressionMethod;
    public int systemMadeBy;

    public int localBitFlag;
    public int centralBitFlag; //TODO are the local and central ones identical?

    public long crc32;

    public long compressedSize;
    public long uncompressedSize;

    public long fileDataOffset;

    @Override
    public long getFileSize() {
        return uncompressedSize;
    }
}
