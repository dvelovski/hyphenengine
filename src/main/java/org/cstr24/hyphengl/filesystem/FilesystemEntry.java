package org.cstr24.hyphengl.filesystem;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Objects;

public abstract class FilesystemEntry {
    public String name;
    public FilesystemEntryType type;

    private HyFileSystem fileSystem;
    private FilesystemEntry parentNode;
    private HashMap<String, FilesystemEntry> childNodes;

    public FilesystemEntry(){
        this("");
    }
    public FilesystemEntry(String _eName){
        this.name = _eName;
        this.childNodes = new HashMap<>();
    }
    public FilesystemEntry setName(String _eName){
        this.name = _eName;
        return this;
    }
    public FilesystemEntry asType(FilesystemEntryType _fsType){
        this.type = _fsType;
        return this;
    }
    public FilesystemEntry setFileSystem(HyFileSystem _fs){
        this.fileSystem = _fs;
        return this;
    }
    public FilesystemEntry asDirectory(){
        this.type = FilesystemEntryType.Directory;
        return this;
    }
    public FilesystemEntry asFile(){
        this.type = FilesystemEntryType.File;
        return this;
    }
    public boolean addChild(FilesystemEntry _toAdd){
        if (_toAdd != this && _toAdd != null){
            childNodes.put(_toAdd.name, _toAdd);
            _toAdd.parentNode = this;

            return true;
        }
        return false;
    }
    public FilesystemEntry getParent(){
        //i.e. if this is the root directory
        return Objects.requireNonNullElse(parentNode, this);
    }
    public FilesystemEntry getEntry(String entryName){
        return childNodes.get(entryName);
    }
    public boolean contains(String entryName){
        return childNodes.containsKey(entryName);
    }
    public HashMap<String, FilesystemEntry> getChildren(){
        return childNodes;
    }

    public HyFileSystem getFileSystem(){
        return this.fileSystem;
    }

    public String toString(){
        return this.name + " - type: " + this.type;
    }

    public abstract long getFileSize();

    public String getFullPath(){
        StringBuilder pathBuilder = new StringBuilder();
        FilesystemEntry currentNode = this;
        while (currentNode != null){
            if (currentNode.type == FilesystemEntryType.Directory){
                pathBuilder.insert(0, "/");
            }
            pathBuilder.insert(0, currentNode.name);
            currentNode = currentNode.parentNode;
        }
        return pathBuilder.toString();
    }

    public boolean hasChildren(){
        return !childNodes.isEmpty();
    }
}
