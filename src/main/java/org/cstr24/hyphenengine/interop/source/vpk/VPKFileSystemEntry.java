package org.cstr24.hyphenengine.interop.source.vpk;

import org.cstr24.hyphenengine.filesystem.FilesystemEntry;

public class VPKFileSystemEntry extends FilesystemEntry {
    int numPreloadBytes;
    byte[] preloadedBytes;
    int archiveIndex;

    int entryOffset;
    int entryLength;

    public VPKFileSystemEntry(){
        super();
    }
    public VPKFileSystemEntry(String _eName) {
        super(_eName);
    }

    @Override
    public long getFileSize() {
        return this.entryLength;
    }
}
