package org.cstr24.hyphengl.filesystem;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class HyNativeFile extends HyFile {
    private static final Logger LOGGER = Logger.getLogger(HyNativeFile.class.getName());

    public HyNativeFile(String path) {
        super(path);
    }

    public HyNativeFile(Path path){
        this.filePath = path;
        //System.out.println("HyNativeFile " + path);
    }

    @Override
    public boolean create(boolean overwriteIfExisting) {
        try {
            Files.createFile(filePath);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean exists() {
        return Files.exists(this.filePath);
    }

    @Override
    public boolean delete() throws IOException {
        return Files.deleteIfExists(filePath);
    }

    @Override //this method loads the entire thing (read-only)
    public ByteBuffer getFileAsByteBuffer() throws IOException {
        //System.out.println("hynativefile as bytebuffer");
        FileChannel fCh = FileChannel.open(this.filePath);
        return fCh.map(FileChannel.MapMode.READ_ONLY, 0, fCh.size());
    }

    public InputStream getFileAsStream() throws FileNotFoundException {
        return new FileInputStream(this.filePath.toFile());
    }

    @Override
    public String readString() throws IOException {
        return Files.readString(this.filePath);
    }

    @Override //this method maps it to memory instead
    public MappedByteBuffer mapFile() throws IOException{
        return mapFile(null);
    }
    @Override
    public MappedByteBuffer mapFile(Set<OpenOption> options) throws IOException{
        var channel = FileChannel.open(
            this.filePath, (options == null ? Set.of() : options)
        );
        long fileSize = channel.size();
        long position = 0L;

        return channel.map(FileChannel.MapMode.READ_ONLY, position, fileSize);
    }

    @Override
    public long getFileSize() throws IOException {
        return Files.size(this.filePath);
    }

    @Override
    public FilesystemEntryType getType() {
        if (Files.isDirectory(this.filePath)){
            return FilesystemEntryType.Directory;
        }else if (Files.isRegularFile(this.filePath)){
            return FilesystemEntryType.File;
        }
        return FilesystemEntryType.Unknown;
    }

    @Override
    public HashMap<String, FilesystemEntry> getChildren() {
        HashMap<String, FilesystemEntry> results;

        if (Files.isDirectory(this.filePath)){
            results = new HashMap<>();
            try (DirectoryStream<Path> pathStream = Files.newDirectoryStream(this.filePath)){
                pathStream.forEach(path -> {
                    FilesystemEntry fsEntry = new FilesystemEntry() {
                        @Override
                        public long getFileSize() {
                            return 0;
                        }
                    };
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
