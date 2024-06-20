package org.cstr24.hyphenengine.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public abstract class HyFile {
    public static final int SEARCH_FILE_ENTRIES = 0x1;
    public static final int SEARCH_DIRECTORY_ENTRIES = 0x2;

    public static final int SEARCH_OPTION_ALL_TYPES = SEARCH_FILE_ENTRIES | SEARCH_DIRECTORY_ENTRIES;

    public static final int SEARCH_NAME_BEGINS_WITH = 0x128;
    public static final int SEARCH_NAME_ENDS_WITH = 0x256;
    public static final int SEARCH_NAME_CONTAINS = 0x512;

    //for the path match search
    public static final int SEARCH_MATCH_FILENAME = 0x1024;
    public static final int SEARCH_INCLUDE_EXTENSION = 0x2048;

    public static final int DEFAULT_SEARCH_FLAGS = SEARCH_OPTION_ALL_TYPES | SEARCH_NAME_BEGINS_WITH;

    protected Path filePath;

    protected HyFile() {

    }
    public HyFile(String path){
        this.filePath = Paths.get(path);
    }

    public boolean create() throws IOException{
        return create(false);
    }
    public abstract boolean create(boolean overwriteIfExisting) throws IOException;
    public abstract boolean exists();
    public abstract boolean delete() throws IOException;
    public abstract ByteBuffer getFileAsByteBuffer() throws IOException;
    public abstract String readString() throws IOException;

    public abstract ByteBuffer mapFile() throws IOException;
    public abstract ByteBuffer mapFile(Set<OpenOption> options) throws IOException;

    public abstract long getFileSize() throws IOException;
    public abstract FilesystemEntryType getType();

    public Path getFilePath(){
        return this.filePath;
    }
    public String getPathString(){
        return this.filePath.toString();
    }
    public String getFileName(){
        return this.filePath.getFileName().toString();
    }
    public String getFileExtension() {
        var fileName = this.filePath.getFileName().toString();
        int extDot = fileName.lastIndexOf('.');
        if (extDot == -1 || extDot == fileName.length() - 1) {
            return "";
        } else {
            return fileName.substring(extDot);
        }
    }
    public String getFileNameNoExt(){
        String result = getFileName();
        if (result.indexOf('.') > -1){
            int lastIdxOfDot = result.lastIndexOf('.');
            result = result.substring(0, lastIdxOfDot);
        }
        return result;
    }
    public abstract HashMap<String, FilesystemEntry> getChildren();

    public static HyFile get(String path){
        return HyFilesystemManager.get().getFile(Paths.get(path));
    }
    public static HyFile get(Path path){
        return HyFilesystemManager.get().getFile(path);
    }

    public static ArrayList<HyFile> search(String searchString){
        return search(searchString, DEFAULT_SEARCH_FLAGS);
    }
    public static ArrayList<HyFile> search(String searchString, int searchFlags){
        return HyFilesystemManager.get().searchFile(searchString, searchFlags);
    }

    public static ArrayList<HyFile> matchesByPath(String path){
        return matchesByPath(path, SEARCH_FILE_ENTRIES | SEARCH_MATCH_FILENAME);
    }
    public static ArrayList<HyFile> matchesByPath(String path, int searchFlags){
        return HyFilesystemManager.get().findMatchingPaths(path, searchFlags);
    }

    public String toString(){
        return this.getClass().getSimpleName() + " (" + getType() + ") @ " + filePath.toString();
    }
}
