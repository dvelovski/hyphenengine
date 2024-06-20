package org.cstr24.hyphenengine.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;

public class HyVirtualFile extends HyFile {

    private HyFileSystem fileSystem;
    FilesystemEntry fileHandle;

    public HyVirtualFile(String path) {
        super(path);
    }

    public HyVirtualFile(Path path){
        this.filePath = path;
        //System.out.println("HyVirtualFile " + path);
    }

    public HyVirtualFile setHandle(FilesystemEntry fileHandle){
        this.fileHandle = fileHandle;
        this.fileSystem = fileHandle.getFileSystem();
        return this;
    }

    public HyVirtualFile setFilesystem(HyFileSystem fileSystem){
        this.fileSystem = fileSystem;
        return this;
    }
    @Override
    public boolean create(boolean overwriteIfExisting) throws IOException {
        return this.fileSystem.createFile(filePath);
    }

    @Override
    public boolean exists() {
        return this.fileSystem.exists(this.filePath);
    }

    @Override
    public boolean delete() {
        return this.fileSystem.deleteFile(this.filePath);
    }

    @Override
    public ByteBuffer getFileAsByteBuffer() throws IOException {
        return this.fileSystem.getFileByteBuffer(fileHandle);
    }

    @Override
    public String readString() throws IOException {
        var byteBuffer = this.fileSystem.getFileByteBuffer(fileHandle);

        byte[] fileBytes = new byte[byteBuffer.limit()];
        byteBuffer.get(fileBytes);

        return new String(fileBytes);
    }

    @Override
    public ByteBuffer mapFile() throws IOException {
        return this.fileSystem.getFileByteBuffer(filePath);
    }

    @Override
    public ByteBuffer mapFile(Set<OpenOption> options) throws IOException {
        return this.fileSystem.getFileByteBuffer(filePath);
    }

    @Override
    public long getFileSize() throws IOException {
        return this.fileHandle.getFileSize();
    }

    @Override
    public FilesystemEntryType getType() {
        return this.fileHandle.type;
    }

    public FilesystemEntry getHandle(){
        return this.fileHandle;
    }

    @Override
    public HashMap<String, FilesystemEntry> getChildren() {
        if (this.getType() == FilesystemEntryType.Directory){
            return this.fileHandle.getChildren();
        }
        return null;
    }
}
