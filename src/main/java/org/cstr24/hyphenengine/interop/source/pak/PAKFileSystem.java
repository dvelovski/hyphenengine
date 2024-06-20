package org.cstr24.hyphenengine.interop.source.pak;

import info.ata4.io.buffer.ByteBufferInputStream;
import org.apache.commons.io.IOUtils;
import org.cstr24.hyphenengine.filesystem.*;
import org.cstr24.hyphenengine.interop.source.pak.structs.ZIPCentralDirecoryFileHeader;
import org.cstr24.hyphenengine.interop.source.pak.structs.ZIPEndOfCentralDirRecord;
import org.cstr24.hyphenengine.interop.source.pak.structs.ZIPFileHeader;
import org.tukaani.xz.LZMAInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Supplier;

public class PAKFileSystem extends HyFileSystem {
    private Path pakPath;
    private String pakName;

    private PAKFileSystemPersistence persistenceMode;

    private long pakOffset;
    private long pakLength;
    private ByteBuffer pakBytes;

    public PAKFileSystem(String _pakPath){ //this would mean it's NOT resident
        this(_pakPath, 0L, 0L);
    }
    public PAKFileSystem(String _pakPath, long _pOffset, long _pLength){
        //we want to be able to seek around in the file.
        //therefore the PAK file needs to be opened as a MappedByteBuffer
        this.persistenceMode = PAKFileSystemPersistence.OnDisk;
        this.pakPath = Paths.get(_pakPath);
        this.pakOffset = _pOffset;

        try {
            FileChannel pakChannel = FileChannel.open(this.pakPath);
            this.pakLength = pakChannel.size();

            //This way I can support loading from a file where the whole file is NOT the PAK
            //No bounds checks yet
            //System.out.println("*** hmm *** " + pakChannel.size());
            this.pakBytes = pakChannel.map(FileChannel.MapMode.READ_ONLY, this.pakOffset, this.pakLength);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        initialize();
    }
    public PAKFileSystem(ByteBuffer pakContents){
        this.persistenceMode = PAKFileSystemPersistence.ResidentInMemory;
        this.pakPath = null; //TODO some kind of constant for "memory" OR perhaps use the parent BSP

        this.pakOffset = 0L;
        this.pakLength = pakContents.limit();

        this.pakBytes = pakContents;

        initialize();
    }

    public void setPakPath(Path path){
        this.pakPath = path;
    }

    @Override
    public HyFileSystem initialize() {
        this.fileStore = new HyFileStore(this, new PAKFileSystemEntry().asDirectory());
        this.fileStore.setFilesystemEntrySupplier(
            (Supplier<PAKFileSystemEntry>) PAKFileSystemEntry::new
        );
        return this;
    }

    public boolean load(){
        ZIPEndOfCentralDirRecord endOfCentralDirRecord = null;
        ArrayList<ZIPCentralDirecoryFileHeader> centralFileHeaders = new ArrayList<>();
        ArrayList<ZIPFileHeader> localFileHeaders = new ArrayList<>();

        pakBytes.order(ByteOrder.LITTLE_ENDIAN);
        pakBytes.position(0);
        pakBytes.position(pakBytes.capacity());

        this.setFileSystemName(this.pakPath.toString());

        //System.out.println("here " + pakPath.toString() + " -> C: " + pakBytes.capacity() + " | L: " + pakBytes.limit() + " | P: " + pakBytes.position());
        //find the 'end of central directory record' signature
        int bufferPointer = pakBytes.position() - 4;
        while (bufferPointer > 0){
            int testInt = pakBytes.getInt(bufferPointer);

            if (testInt == ZipConstants.END_OF_CENTRAL_DIRECTORY_SIGNATURE){
                //System.out.println("found signature int at " + bufferPointer);
                endOfCentralDirRecord = new ZIPEndOfCentralDirRecord().parseStruct(pakBytes, bufferPointer);
                break;
            }
            bufferPointer --;
        }

        //compose the central directory
        if (endOfCentralDirRecord != null){
            pakBytes.position((int) endOfCentralDirRecord.centralDirectoryOffset);
            int count = 0;
            while (pakBytes.hasRemaining() && pakBytes.remaining() > 4){ //make sure there's at least enough room for an int, otherwise we'll get an underflow exception
                int signature = pakBytes.getInt();
                bufferPointer = pakBytes.position();

                if (signature == ZipConstants.CENTRAL_DIRECTORY_ENTRY_SIGNATURE){
                    //System.out.println("found signature " + (++count));
                    var centralDirHeader = new ZIPCentralDirecoryFileHeader().parseStruct(pakBytes, bufferPointer - 4);
                    centralFileHeaders.add(centralDirHeader);

                    int prevPointer = pakBytes.position();
                    pakBytes.position((int) centralDirHeader.localFileHeaderRelativeOffset);
                    var lFileHeader = new ZIPFileHeader().parse(pakBytes);
                    localFileHeaders.add(lFileHeader);

                    pakBytes.position(prevPointer);
                }
            }

            //System.out.println("Zip Stats:");
            //System.out.println("Received: " + localFileHeaders.size());
            //System.out.println("Expected " + endOfCentralDirRecord.totalCentralDirectoryRecords + " files, got: " + centralFileHeaders.size() + " and " + localFileHeaders.size());

            for (int i = 0; i < localFileHeaders.size(); i++){
                var localHeader = localFileHeaders.get(i);
                var centralHeader = centralFileHeaders.get(i);

                var pakFileEntry = (PAKFileSystemEntry) fileStore.addFileHierarchical(Paths.get(localHeader.fileName));
                if (pakFileEntry != null){
                    pakFileEntry.crc32 = centralHeader.uncompressedCRC32;
                    pakFileEntry.compressedSize = centralHeader.compressedSize;
                    pakFileEntry.uncompressedSize = centralHeader.uncompressedSize;
                    pakFileEntry.fileDataOffset = localHeader.fileDataOffset; //the physical offset into the PAK data

                    pakFileEntry.compressionMethod = localHeader.compressionMethod;
                    pakFileEntry.systemMadeBy = centralHeader.versionMadeBy;
                    pakFileEntry.centralBitFlag = centralHeader.bitFlag;
                    pakFileEntry.localBitFlag = localHeader.bitFlag;

                    //System.out.println("Compression method: " + ZipConstants.compressionMethodToString(pakFileEntry.compressionMethod));
                    //System.out.println("Made by: " + ZipConstants.versionMadeToString(pakFileEntry.systemMadeBy));
                }
            }

            return true;
        }
        return false;
    }
    public void loadAndMount(){
        loadAndMount(0);
    }
    public void loadAndMount(int priority){
        if (load()){
            HyFilesystemManager.get().mount(this, priority);
        }
    }

    @Override
    public FilesystemEntry getEntry(Path path) {
        return fileStore.getEntry(path);
    }

    @Override
    public ByteBuffer getFileByteBuffer(Path path) throws IOException {
        var entry = getEntry(path);
        if (entry != null && entry.type == FilesystemEntryType.File){
            return getFileByteBuffer(entry);
        }
        throw new FileNotFoundException("Could not find: " + path + " in PAK FileSystem " + this.getFileSystemName());
    }

    @Override
    public ByteBuffer getFileByteBuffer(FilesystemEntry entry) throws IOException {
        if (entry.type == FilesystemEntryType.File){
            var pakFileEntry = (PAKFileSystemEntry) entry;
            ByteBuffer pakBuff = null;

            switch (pakFileEntry.compressionMethod){
                case ZipConstants.METHOD_LZMA -> {
                    System.out.println("LZMA compression detected: " + entry.name);
                    pakBuff = pakBytes.slice((int) pakFileEntry.fileDataOffset, (int) pakFileEntry.compressedSize).order(ByteOrder.LITTLE_ENDIAN);
                    int majorVersion = pakBuff.get();
                    int minorVersion = pakBuff.get();
                    int size = pakBuff.getShort() & 0xFFFF;
                    if (size != 5){
                        throw new UnsupportedEncodingException("Size != 5 - " + size + ": major,minor: " + majorVersion + "," + minorVersion);
                    }else{
                        System.out.println("size detected: " + size + ": major,minor: " + majorVersion + "," + minorVersion);
                    }

                    byte propertyByte = pakBuff.get();
                    int dictSize = pakBuff.getInt();

                    long uncompressedSize;
                    if ((pakFileEntry.centralBitFlag & (1 << 1)) != 0){
                        uncompressedSize = -1L;
                    }else{
                        uncompressedSize = pakFileEntry.uncompressedSize;
                    }

                    try (LZMAInputStream lzmaIn = new LZMAInputStream(new ByteBufferInputStream(pakBuff), uncompressedSize, propertyByte, dictSize)){
                        pakBuff = ByteBuffer.wrap(IOUtils.toByteArray(lzmaIn)).order(ByteOrder.LITTLE_ENDIAN);
                        //TODO do I need to keep Apache Commons IO utils for this?
                    }
                }
                default -> {
                    //No compression
                    pakBuff = pakBytes.slice((int) pakFileEntry.fileDataOffset, (int) pakFileEntry.uncompressedSize);
                }
            }

            //System.out.println(MemoryUtil.memUTF8(pakBuff));

            return pakBuff;
        }
        throw new IOException("Cannot provide a ByteBuffer for a " + entry.type + ".");
    }

    @Override
    public boolean createFile(Path path) {
        System.out.println("Creating / adding files to PAKfiles is not supported.");
        return false;
    }

    @Override
    public boolean deleteFile(Path path) {
        System.out.println("Deleting files from PAKfiles is not supported.");
        return false;
    }

    @Override
    public ArrayList<FilesystemEntry> getDirectoryContents(Path path) {
        return null;
    }

    @Override
    public FilesystemEntry getRoot() {
        return fileStore.getRoot();
    }

    @Override
    public void unmount() {
        fileStore.reset();
    }
}
