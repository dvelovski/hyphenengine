package org.cstr24.hyphenengine.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class HyFilesystemManager {
    public static HyFilesystemManager instance;

    //mounted file systems
    private final ArrayList<MountedFS> mountedFileSystems;
    private final ArrayList<NativeSearchDirectory> searchDirectories;

    public static HyFilesystemManager get() {
        if (instance == null) {
            instance = new HyFilesystemManager();
        }
        return instance;
    }

    public HyFilesystemManager(){
        mountedFileSystems = new ArrayList<>();
        searchDirectories = new ArrayList<>();
    }

    public void mount(HyFileSystem fs){
        mount(fs, 0);
    }
    public void mount(HyFileSystem fs, int priority){
        mountedFileSystems.add(new MountedFS(fs, priority));
        mountedFileSystems.sort((o1, o2) -> Integer.compare(o2.priority, o1.priority));
    }
    public void addNativeSearchDirectory(String pathToDirectory){
        addNativeSearchDirectory(Paths.get(pathToDirectory));
    }
    public void addNativeSearchDirectory(Path pathToDirectory){
        try (Stream<Path> files = Files.walk(pathToDirectory)){
            files.forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HyFile getFile(Path path){
        //fs manager
        //can call fs resolvers
        //fs resolvers can have priority
        //i.e. source stuff - PAK > VPK > base FS
        //if no resolvers find the data we just return a HyFile wrapping a regular java File
        FilesystemEntry handle = null;
        for (MountedFS mountedSystem : mountedFileSystems) {
            handle = mountedSystem.fileSystem.getEntry(path);

            if (handle != null) {
                return new HyVirtualFile(path).setHandle(handle);
            }
        }

        //System.out.println("could not find file virtually: " + path);

        //if we found a file, it would've been returned by now
        for (NativeSearchDirectory searchDir : searchDirectories){

        }

        return new HyNativeFile(path);
    }
    public ArrayList<HyFile> searchFile(String fileName, int searchFlags){
        ArrayList<FilesystemEntry> locatedEntries = new ArrayList<>();
        ArrayList<HyFile> fileResults = new ArrayList<>();

        for (MountedFS mountedSystem : mountedFileSystems){
            locatedEntries.addAll(mountedSystem.fileSystem.search(fileName, searchFlags));
        }

        locatedEntries.forEach(entry -> {
            String filePath = entry.getFullPath();
            //System.out.println("located path: " + filePath);
            fileResults.add(new HyVirtualFile(filePath).setHandle(entry));
        });

        return fileResults;
    }
    public ArrayList<HyFile> findMatchingPaths(String path, int searchFlags){
        return findMatchingPaths(Paths.get(path), searchFlags);
    }
    public ArrayList<HyFile> findMatchingPaths(Path path, int searchFlags){
        ArrayList<FilesystemEntry> locatedEntries = new ArrayList<>();
        ArrayList<HyFile> fileResults = new ArrayList<>();

        for (MountedFS mountedSystem : mountedFileSystems){
            locatedEntries.addAll(mountedSystem.fileSystem.findMatchingPaths(path, searchFlags));
        }

        locatedEntries.forEach(entry -> {
            String filePath = entry.getFullPath();
            fileResults.add(new HyVirtualFile(filePath).setHandle(entry));
        });

        return fileResults;
    }

    private static class MountedFS{
        public HyFileSystem fileSystem;
        public int priority;

        public MountedFS(HyFileSystem fileSystem){
            this(fileSystem, 0);
        }
        public MountedFS(HyFileSystem fileSystem, int priority){
            this.fileSystem = fileSystem;
            this.priority = priority;
        }
    }
}
