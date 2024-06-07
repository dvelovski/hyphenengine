package org.cstr24.hyphengl.interop.source.vpk;

import org.cstr24.hyphengl.filesystem.*;
import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.vpk.structs.VPKDirectoryEntry;
import org.cstr24.hyphengl.interop.source.vpk.structs.VPKHeader;
import org.cstr24.hyphengl.interop.source.vpk.structs.VPKHeader_V1;
import org.cstr24.hyphengl.interop.source.vpk.structs.VPKHeader_V2;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class VPKFileSystem extends HyFileSystem {
    private Path vpkPath;
    private String archiveName = "";

    private HashMap<Integer, SeekableByteChannel> archiveBuffers;
    private ArrayList<Path> partPathList = new ArrayList<>();

    public VPKFileSystem(String pathToVPK){
        this.vpkPath = Paths.get(pathToVPK);
        this.archiveBuffers = new HashMap<>();

        this.setFileSystemName(pathToVPK);

        initialize();
    }

    @Override
    public HyFileSystem initialize() {
        this.fileStore = new HyFileStore(this,
            new VPKFileSystemEntry("").asDirectory()
        );
        fileStore.setFilesystemEntrySupplier(
            (Supplier<VPKFileSystemEntry>) VPKFileSystemEntry::new
        );
        return this;
    }

    public void loadAndMount(){
        loadAndMount(0);
    }
    public void loadAndMount(int priority){
        if (load()){
            HyFilesystemManager.get().mount(this, priority);
        }
    }

    public boolean load(){
        if (Files.exists(vpkPath)){
            try {
                var fileBuffer = HyFile.get(vpkPath).getFileAsByteBuffer().order(ByteOrder.LITTLE_ENDIAN);

                int magic = fileBuffer.getInt();
                int version = fileBuffer.getInt();

                VPKHeader header;
                switch (version) {
                    case 1 -> header = new VPKHeader_V1().parse(fileBuffer);
                    case 2 -> header = new VPKHeader_V2().parse(fileBuffer);
                    default -> throw new UnsupportedClassVersionError("Unsupported VPK version: " + version);
                }
                //System.out.println("File version: " + version);
                //System.out.println("pos: " + buf.position());

                while (true){
                    String extension = header.nullTerminatedString(fileBuffer, 1024);
                    if (extension.isEmpty()){
                        break;
                    }
                    while (true){
                        String path = header.nullTerminatedString(fileBuffer, 1024);
                        if (path.isEmpty()){
                            break;
                        }

                        while (true){
                            String filename = header.nullTerminatedString(fileBuffer, 1024);
                            if (filename.isEmpty() || filename.isBlank()){
                                break;
                            }else{
                                var dirEntry = new VPKDirectoryEntry().parse(fileBuffer);

                                //vpkFS.createFile(path + "/" + filename + "." + extension);
                                //vpkFS.createFile(path + "/");
                                String fileDir = (path.isBlank() ? "" : path + "/");
                                String filePath = (fileDir + filename + "." + extension).trim();
                                var vpkFileEntry = (VPKFileSystemEntry) fileStore.addFileHierarchical(Paths.get(filePath));

                                if (vpkFileEntry != null){
                                    vpkFileEntry.archiveIndex = dirEntry.archiveIndex;
                                    vpkFileEntry.entryOffset = dirEntry.entryOffset;
                                    vpkFileEntry.entryLength = dirEntry.entryLength;
                                    vpkFileEntry.numPreloadBytes = dirEntry.numPreloadBytes;
                                    vpkFileEntry.preloadedBytes = dirEntry.preloadedBytes;
                                }
                                //System.out.println("File: " + filename + "." + extension);

                            }
                        }
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

            String pakFilename = vpkPath.getFileName().toString();
            pakFilename = pakFilename.substring(0, pakFilename.lastIndexOf('.'));

            //System.out.println(vpkPath.getFileName() + " / " + pakFilename);

            if (pakFilename.endsWith("_dir")){
                archiveName = pakFilename.substring(0, pakFilename.length() - 4);

                try {
                    partPathList = Files.list(vpkPath.getParent())
                            .filter(Files::isRegularFile)
                            .filter(file -> file.getFileName().toString().matches(archiveName + "_(\\d)+\\.vpk"))
                            .collect(Collectors.toCollection(ArrayList::new));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                archiveName = pakFilename;
            }
            return true;
        }
        return false;
    }

    @Override
    public ByteBuffer getFileByteBuffer(Path path) throws IOException {
        var entry = getEntry(path);
        if (entry != null){
            return getFileByteBuffer(entry);
        }
        throw new FileNotFoundException("Could not find: " + path + " in PAK FileSystem " + this.getFileSystemName());
    }

    @Override
    public ByteBuffer getFileByteBuffer(FilesystemEntry entry) throws IOException {
        if (entry.type == FilesystemEntryType.File){
            var vpkFileEntry = (VPKFileSystemEntry) entry;

            long bufferOffset = 0L;

            if (vpkFileEntry.preloadedBytes != null){
                System.out.println("FILE HAS PRE-LOADED BYTES");
                if (vpkFileEntry.numPreloadBytes >= vpkFileEntry.entryLength){
                    return ByteBuffer.wrap(vpkFileEntry.preloadedBytes);
                }else{
                    System.out.println("Pre-loaded bytes: " + vpkFileEntry.numPreloadBytes + " vs. file length: " + vpkFileEntry.entryLength);
                    bufferOffset = vpkFileEntry.numPreloadBytes;
                }
            }
            //which file are we in?
            //System.out.println(entry.name + " belongs in archive " + vpkFileEntry.archiveIndex + " and is " + vpkFileEntry.entryLength + " bytes long.");
            Path pathToUse = partPathList.get(vpkFileEntry.archiveIndex);

            SeekableByteChannel vpkFileChannel = null;

            if (archiveBuffers.containsKey(vpkFileEntry.archiveIndex)){
                //System.out.println("we have not had to re-open this file: " + pathToUse);
                vpkFileChannel = archiveBuffers.get(vpkFileEntry.archiveIndex);
            }else{
                vpkFileChannel = Files.newByteChannel(pathToUse, StandardOpenOption.READ);
                archiveBuffers.put(vpkFileEntry.archiveIndex, vpkFileChannel);
            }

            /*//I love Slice but FileChannels don't let you do that
            vpkFileChannel.position(vpkFileEntry.entryOffset);
            vpkFileChannel.read(bufferToReturn);*/

            return channelSlice(vpkFileChannel, vpkFileEntry.entryOffset, bufferOffset, vpkFileEntry.entryLength);

        }else{
            throw new IOException("Cannot provide a ByteBuffer for a " + entry.type + " @ " + entry.getFullPath());
        }
    }

    public ByteBuffer channelSlice(SeekableByteChannel channel, long position, long bufferOffset, long length) throws IOException {
        ByteBuffer buff = ByteBuffer.allocateDirect((int) length);
        channel.position(position);
        channel.read(buff);

        buff.rewind();

        return buff;
    }

    @Override
    public boolean createFile(Path path) {
        System.out.println("Creating / adding files to .vpks is not supported.");
        return false;
    }

    @Override
    public boolean deleteFile(Path path) {
        System.out.println("Deletion of files from .vpks is not supported.");
        return false;
    }

    @Override
    public FilesystemEntry getEntry(Path path) {
        /*if (path.toString().equals("/")){ //it appears the root dir is represented by a space in VPK
            //System.out.println("root? " + path);
            //return fileStore.getRoot();
        }*/
        //^ i don't like this any more as every file system has a root.
        //  highest priority FS will always have its root chosen which is meaningless
        //  if you want to access the root (presumably to iterate) you can grab it directly from the FS

        var result = fileStore.getEntry(path);
        return result;
    }

    @Override
    public ArrayList<FilesystemEntry> getDirectoryContents(Path path) {
        var dirEntry = getEntry(path);
        if (dirEntry != null){
            if (dirEntry.type == FilesystemEntryType.Directory){
                return new ArrayList<>(dirEntry.getChildren().values());
            }else{
                throw new IllegalArgumentException(path + ": not a directory!");
            }
        }else{
            throw new NullPointerException(path + ": does not exist.");
        }
    }

    @Override
    public FilesystemEntry getRoot() {
        return fileStore.getRoot();
    }

    @Override
    public void unmount() {
        //clear all the entries
        fileStore.reset();

        //clear the buffer references
        archiveBuffers.values().forEach(channel -> {
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        archiveBuffers.clear();

        //clear the path list (re-mounting rebuilds the list anyway)
        partPathList.clear();
    }
}
