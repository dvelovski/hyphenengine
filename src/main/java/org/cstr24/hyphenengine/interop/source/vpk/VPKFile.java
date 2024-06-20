package org.cstr24.hyphenengine.interop.source.vpk;

// PAKWATCH!!!

import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.interop.source.pak.PAKFileSystem;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class VPKFile {
    private Path originalPath;

    private String archiveName; //the name of the archive without any extension / pack identifiers
    private ArrayList<Path> partPaths;

    private VPKFileSystem vpkFS;

    public static void main(String[] args) {
        var theVPK = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Portal\\portal\\portal_pak_dir.vpk");
        theVPK.loadAndMount();

        var thePAK = new PAKFileSystem("pakdump.bin");
        thePAK.loadAndMount();

        try {
            System.out.println("*** cake ***");
            var bb = theVPK.getFileByteBuffer("materials/models/props/cake/cake.vmt");
            String vmt = MemoryUtil.memUTF8(bb);
            System.out.println(vmt);

            //var env = new KeyValueParser().parse(vmt).treeRoot.getChild("LightmappedGeneric_DX9").getChild("$envmap").getValue();
            //System.out.println("envmap: " + env);

            System.out.println("*** saucepan ***");
            bb = theVPK.getFileByteBuffer("materials/models/props/saucepan/saucepan.vmt");
            vmt = MemoryUtil.memUTF8(bb);
            System.out.println(vmt);

            System.out.println("*** food_can ***");
            /*bb = theVPK.getFileByteBuffer("materials/models/props/food_can/food_can.vmt");
            vmt = MemoryUtil.memUTF8(bb);
            System.out.println(vmt);
*/
            var l = HyFile.matchesByPath("materials/models/props/cake/cake");
            System.out.println(l);

            var l2 = theVPK.search("cake");
            System.out.println(l2);

            var l3 = HyFile.search("cake", HyFile.SEARCH_FILE_ENTRIES | HyFile.SEARCH_NAME_BEGINS_WITH);
            System.out.println(l3);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
