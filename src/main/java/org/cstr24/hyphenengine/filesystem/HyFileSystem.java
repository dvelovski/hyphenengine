package org.cstr24.hyphenengine.filesystem;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;

public abstract class HyFileSystem {
    public static final String DEFAULT_PATH_DELIMETER = "/";
    //a mechanism for 'mounting' file systems outside the OS's own
    //i'm thinking of allowing this for anything that doesn't reference a file such as VPKs and possibly BSP PAKs
    private String protocol;
    private String fsName;

    public abstract HyFileSystem initialize();

    public abstract FilesystemEntry getEntry(Path path);

    protected HyFileStore fileStore;
    public HyFileStore getFileStore(){
        return this.fileStore;
    }

    public ByteBuffer getFileByteBuffer(String path) throws IOException{
        return getFileByteBuffer(Paths.get(path));
    }
    public abstract ByteBuffer getFileByteBuffer(Path path) throws IOException;
    public abstract ByteBuffer getFileByteBuffer(FilesystemEntry entry) throws IOException;

    public abstract boolean createFile(Path path);
    public abstract boolean deleteFile(Path path);

    public boolean exists(Path path) {
        return getEntry(path) != null;
    }
    public abstract ArrayList<FilesystemEntry> getDirectoryContents(Path path);
    public abstract FilesystemEntry getRoot();

    public void mount(){
        HyFilesystemManager.get().mount(this);
    }
    public void mount(int priority){
        HyFilesystemManager.get().mount(this, priority);
    }

    public abstract void unmount();

    public void setFileSystemName(String newName){
        this.fsName = newName;
    }
    public String getFileSystemName(){
        return this.fsName;
    }

    public ArrayList<FilesystemEntry> findMatchingPaths(String path, int searchFlags){
        return findMatchingPaths(Paths.get(path), searchFlags);
    }
    public ArrayList<FilesystemEntry> findMatchingPaths(Path path, int searchFlags){ //TODO honour the filters like we use with searches - files or folders? also case-sensitivity
        ArrayList<FilesystemEntry> results = new ArrayList<>();

        if (fileStore != null){
            var dirToSearch = fileStore.getRoot();
            int numPathComponents = path.getNameCount();

            for (int i = 0; i < numPathComponents; i++){
                String currentComponent = path.getName(i).toString();
                //System.out.println(this.fsName + ": " + i + " - " + currentComponent + " - path components: " + numPathComponents);

                var dirChildren = dirToSearch.getChildren().entrySet();
                //dirChildren.toString();

                if (i == numPathComponents - 1){
                    Predicate<Map.Entry<String, FilesystemEntry>> filenamePredicate = (entry) -> true;
                    Predicate<Map.Entry<String, FilesystemEntry>> entryTypePredicate;

                    if ((searchFlags & HyFile.SEARCH_MATCH_FILENAME) != 0){
                        //filename must match 'currentComponent' except the extension.
                        filenamePredicate = entry -> {
                            String entryName = entry.getKey();
                            if ((searchFlags & HyFile.SEARCH_INCLUDE_EXTENSION) == 0) {
                                int lastDotIndex = entryName.lastIndexOf('.');
                                if (lastDotIndex != -1){
                                    entryName = entryName.substring(0, lastDotIndex);
                                }
                            }
                            return entryName.equalsIgnoreCase(currentComponent);
                        };
                    }else if ((searchFlags & HyFile.SEARCH_NAME_BEGINS_WITH) != 0){
                        filenamePredicate = entry -> entry.getKey().toLowerCase().startsWith(currentComponent.toLowerCase());
                    }

                    entryTypePredicate = (entry) -> {
                        if ((searchFlags & HyFile.SEARCH_FILE_ENTRIES) != 0){
                            return entry.getValue().type == FilesystemEntryType.File;
                        }
                        if ((searchFlags & HyFile.SEARCH_DIRECTORY_ENTRIES) != 0){
                            return entry.getValue().type == FilesystemEntryType.Directory;
                        }
                        return false;
                    };

                    dirChildren.stream()
                        .filter(filenamePredicate)
                        .filter(entryTypePredicate)
                        .forEach((entry) -> results.add(entry.getValue()));
                }else{
                    dirToSearch = null; //reset

                    for (Map.Entry<String, FilesystemEntry> entry : dirChildren) {
                        if (entry.getKey().equalsIgnoreCase(currentComponent)) {
                            dirToSearch = entry.getValue();
                            //System.out.println("\thave found next dir: " + dirToSearch);
                            break;
                        }
                    }

                    if (dirToSearch == null){
                        break;
                    }
                }
            }
        }

        return results;
    }

    public ArrayList<FilesystemEntry> search(String name){
        return search(name, HyFile.DEFAULT_SEARCH_FLAGS);
    }

    public ArrayList<FilesystemEntry> search(String name, int searchFlags){
        ArrayList<FilesystemEntry> results = new ArrayList<>();

        if (fileStore != null){
            enumerateEntryForMatches(name, fileStore.getRoot(), searchFlags, results);
        }

        return results;
    }
    private void enumerateEntryForMatches(String needle, FilesystemEntry haystack, int searchFlags, ArrayList<FilesystemEntry> results){
        Stack<FilesystemEntry> toEnter = new Stack<>();
        haystack.getChildren().forEach((key, value) -> {
            if (value.type == FilesystemEntryType.Directory && value.hasChildren()){
                toEnter.add(value);
            }
            if (value.type == FilesystemEntryType.Directory && ((searchFlags & HyFile.SEARCH_DIRECTORY_ENTRIES) == 0)){
                //skip
            }else if (value.type == FilesystemEntryType.File && ((searchFlags & HyFile.SEARCH_FILE_ENTRIES) == 0)){
                //skip
            }else{
                if ((searchFlags & HyFile.SEARCH_FILE_ENTRIES) == HyFile.SEARCH_FILE_ENTRIES && value.type == FilesystemEntryType.File ||
                        ((searchFlags & HyFile.SEARCH_DIRECTORY_ENTRIES) == HyFile.SEARCH_DIRECTORY_ENTRIES && value.type == FilesystemEntryType.Directory)){
                    //System.out.println("found a: " + value.type + " and we are looking for that.");

                    if ((searchFlags & HyFile.SEARCH_NAME_CONTAINS) != 0){
                        if (value.name.contains(needle)){
                            results.add(value);
                        }
                    }else if ((searchFlags & HyFile.SEARCH_NAME_BEGINS_WITH) != 0){
                        if (value.name.startsWith(needle)){
                            results.add(value);
                        }
                    }else if ((searchFlags & HyFile.SEARCH_NAME_ENDS_WITH) != 0){
                        if (value.name.endsWith(needle)){
                            results.add(value);
                        }
                    }
                }else{
                    //TODO why the fuck are there so many NULLS
                    //System.out.println("we found a: " + value.type + " and we're not looking for that - " + value + " - " + this.fsName);
                }
            }
        });
        toEnter.forEach(entry -> {
            enumerateEntryForMatches(needle, entry, searchFlags, results);
        });
    }
}
