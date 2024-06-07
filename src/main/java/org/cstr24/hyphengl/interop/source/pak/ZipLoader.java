package org.cstr24.hyphengl.interop.source.pak;

import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.pak.structs.ZIPCentralDirecoryFileHeader;
import org.cstr24.hyphengl.interop.source.pak.structs.ZIPEndOfCentralDirRecord;
import org.cstr24.hyphengl.interop.source.pak.structs.ZIPFileHeader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ZipLoader {
    public ZipLoader(String fileLocation){
        this(HyFile.get(fileLocation));
    }
    public ZipLoader(HyFile fileLocation){

    }
}
