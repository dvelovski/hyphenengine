package org.cstr24.hyphenengine.interop.source.pak;

import org.cstr24.hyphenengine.filesystem.HyFile;

public class ZipLoader {
    public ZipLoader(String fileLocation){
        this(HyFile.get(fileLocation));
    }
    public ZipLoader(HyFile fileLocation){

    }
}
