package org.cstr24.hyphengl.filesystem;

import java.nio.file.Path;
import java.util.function.Supplier;

public class HyFileStore {
    private final HyFileSystem fileSystem;
    private FilesystemEntry rootEntry;

    private Supplier<? extends FilesystemEntry> entrySupplier;

    public HyFileStore(HyFileSystem fileSystem, FilesystemEntry rootEntry){
        this.fileSystem = fileSystem;
        this.rootEntry = rootEntry;
    }

    public HyFileStore setFilesystemEntrySupplier(Supplier<? extends FilesystemEntry> supplier){
        this.entrySupplier = supplier;
        return this;
    }

    public FilesystemEntry getRoot(){
        return this.rootEntry;
    }

    public void clearEntries(){
        rootEntry.getChildren().clear();
    }
    public void reset(){
        rootEntry.getChildren().clear();
        this.rootEntry = null;
    }

    public boolean addEntry(FilesystemEntry newEntry){
        return addEntry(this.rootEntry, newEntry);
    }

    public boolean addEntry(FilesystemEntry parent, FilesystemEntry newEntry){
        parent.addChild(entrySupplier.get());
        return parent.addChild(newEntry);
    }

    public boolean addEntry(Path path, FilesystemEntry newEntry){
        var parent = getEntry(path);
        if (parent != null){
            return addEntry(parent, newEntry);
        }

        return false;
    }

    public FilesystemEntry addFileHierarchical(Path path){
        return addEntry(path, FilesystemEntryType.File, true);
    }
    public FilesystemEntry addEntry(Path path, FilesystemEntryType type, boolean hierarchical){
        FilesystemEntry fileParent = null;
        FilesystemEntry toAdd = null;

        if (path.getNameCount() > 1){
            Path hierarchy = path.subpath(0, path.getNameCount() - 1);
            String lastNameComponent = path.getFileName().toString();

            if (hierarchical){
                addDirectoriesHierarchical(hierarchy);
            }
            fileParent = getEntry(hierarchy);
            if (!fileParent.contains(lastNameComponent)) {
                toAdd = entrySupplier.get()
                        .asFile()
                        .setName(lastNameComponent);
            }
        }else{
            fileParent = rootEntry;
            if (!fileParent.contains(path.toString())) {
                toAdd = entrySupplier.get()
                        .asFile()
                        .setName(path.toString());
            }
        }

        if (toAdd != null){
            toAdd.setFileSystem(this.fileSystem);
            addEntry(fileParent, toAdd);

            if (toAdd.type == null){
                System.out.println("null: " + toAdd.getFullPath());
            }
            //System.out.println("added file: " + toAdd.getFullPath());
        }

        return toAdd;
    }
    public boolean addDirectoriesHierarchical(Path path){
        FilesystemEntry currentNode = getRoot();
        int pathComponentCount = path.getNameCount();

        for (int i = 0; i < pathComponentCount; i++){
            String currentComponent = path.getName(i).toString();

            var currentChild = currentNode.getEntry(currentComponent);
            if (currentChild == null){
                currentChild = entrySupplier.get()
                        .asDirectory()
                        .setName(currentComponent);
                currentChild.setFileSystem(this.fileSystem);

                addEntry(currentNode, currentChild);

                //System.out.println("added " + (i == 0 ? currentComponent : path.subpath(0, i + 1)));
            }
            currentNode = currentChild;
        }

        return true;
    }

    public FilesystemEntry getEntry(Path path){
        var currentNode = rootEntry;
        int pathComponentCount = path.getNameCount();

        for (int i = 0; i < pathComponentCount; i++){
            String currentComponent = path.getName(i).toString().toLowerCase();
            var childNode = currentNode.getEntry(currentComponent);
            if (childNode != null){
                if (i == pathComponentCount - 1){
                    return childNode;
                }
                currentNode = childNode;
            }else{
                break;
            }
        }
        return null;
    }
}
