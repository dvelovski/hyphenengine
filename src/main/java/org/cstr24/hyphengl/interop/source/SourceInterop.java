package org.cstr24.hyphengl.interop.source;

import org.cstr24.hyphengl.assets.AssetCache;
import org.cstr24.hyphengl.assets.AssetLoader;
import org.cstr24.hyphengl.assets.HyResourceCache;
import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.entities.components.BoneMergeComponent;
import org.cstr24.hyphengl.entities.components.StudioModelComponent;
import org.cstr24.hyphengl.interop.source.studiomdl.StudioModelLoader;
import org.cstr24.hyphengl.interop.source.vpk.VPKFileSystem;
import org.cstr24.hyphengl.interop.source.vtf.VTFLoader;
import org.joml.Matrix4f;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

public class SourceInterop {
    public static final int LZMA_ID = encodeID("LZMA");
    public static final int LZMA_HEADER_SIZE = 17;

    public static String readNullTerminatedString(ByteBuffer in, int maxLength, boolean advanceByMaxLength) {
        return readNullTerminatedString(in, in.position(), maxLength, advanceByMaxLength);
    }

    /**
     * Fetches a null-terminated string from 'in', then resets in's position to before the operation was performed.
     * @param in
     * @param startPosition
     * @param maxLength
     * @return
     */
    public static String fetchNullTerminatedString(ByteBuffer in, int startPosition, int maxLength){
        int initialBufferPosition = in.position();
        String returnString = readNullTerminatedString(in, startPosition, maxLength, false);
        in.position(initialBufferPosition);
        return returnString;
    }

    /**
     * Reads a null-terminated string from 'in'.
     * @param in The ByteBuffer from which we're trying to read the null-terminated string.
     * @param startPosition If provided, position will override in's current position, and the String will be read from 'position'.
     * @param maxLength The maximum length that the String to read can be - we won't try to read any more bytes than this.
     * @param advanceByMaxLength If true, the ByteBuffer should be advanced by maxLength (to deal with Strings padded out to a given length).
     *                           If false, the ByteBuffer will only advance by the number of bytes read until it encountered a null terminator.
     * @return
     */
    public static String readNullTerminatedString(ByteBuffer in, int startPosition, int maxLength, boolean advanceByMaxLength){
        String returnString = "";

        in.position(startPosition);

        for (int i = 0; i < maxLength; i++){
            int currentPos = in.position();

            if (in.get() == 0){
                int strLength = currentPos - startPosition;
                byte[] bytes = new byte[strLength];

                in.get(startPosition, bytes, 0, strLength);
                returnString = new String(bytes);

                //advanceByMaxLength is meant to overcome potential parsing problems
                //the problems i forsee are - what if we've got a string that's only 3 chars long
                //but the structure has it padded out to say, 64 byes? we're not advancing the buffer far enough.
                if (advanceByMaxLength){
                    in.position(startPosition + (maxLength));
                }
                //System.out.println("String encountered: " + returnString);

                break;
            }
        }

        return returnString;
    }

    /* https://stackoverflow.com/a/2183259
    * Using a ByteBuffer wrapped then to a byte array would be incredibly ugly */
    public static byte[] intToByteArray(int value){
        byte[] bArr = new byte[4];
        var bBuff = ByteBuffer.allocateDirect(4).putInt(value);
        bBuff.get(0, bArr);
        return bArr;
    }
    public static int encodeID(String code){
        byte[] bytes = code.getBytes();
        return (bytes[3] << 24) | (bytes[2] << 16) | (bytes[1] << 8) | bytes[0];
    }
    public static String stripClass(String inPath){
        return inPath.replaceAll("(_scout|_soldier|_pyro|_demo|_demoman|_engineer|_heavy|_hvyweapons|_sniper|_medic|_spy)\\b", "");
    }

    //matrix4x3 in source land evidently means 4 wide by 3 tall. it's also row major, not column major?
    public static Matrix4f readMatrix4x3(ByteBuffer in){
        Matrix4f m4x3 = new Matrix4f();
        m4x3.m00(in.getFloat());
        m4x3.m10(in.getFloat());
        m4x3.m20(in.getFloat());
        m4x3.m30(in.getFloat());

        m4x3.m01(in.getFloat());
        m4x3.m11(in.getFloat());
        m4x3.m21(in.getFloat());
        m4x3.m31(in.getFloat());

        m4x3.m02(in.getFloat());
        m4x3.m12(in.getFloat());
        m4x3.m22(in.getFloat());
        m4x3.m32(in.getFloat());

        return m4x3;
    }

    public static void mountHalfLife2(){
        var vpkFS1 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Half-Life 2\\hl2\\hl2_textures_dir.vpk");
        vpkFS1.loadAndMount(1);
        var vpkFS2 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Half-Life 2\\hl2\\hl2_misc_dir.vpk");
        vpkFS2.loadAndMount(2);
        var vpkFS3 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Half-Life 2\\hl2\\hl2_pak_dir.vpk");
        vpkFS3.loadAndMount(3);
    }

    public static void mountTeamFortress2(){
        var vpkFS1 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Team Fortress 2\\hl2\\hl2_textures_dir.vpk");
        vpkFS1.loadAndMount(1);
        var vpkFS2 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Team Fortress 2\\hl2\\hl2_misc_dir.vpk");
        vpkFS2.loadAndMount(2);
        var vpkFS3 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Team Fortress 2\\tf\\tf2_textures_dir.vpk");
        vpkFS3.loadAndMount(3);
        var vpkFS4 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Team Fortress 2\\tf\\tf2_misc_dir.vpk");
        vpkFS4.loadAndMount(3);
    }

    public static void mountLeft4Dead2(){
        var vpkFS1 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Left 4 Dead 2\\left4dead2\\pak01_dir.vpk");
        vpkFS1.loadAndMount(1);
        var vpkFS2 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Left 4 Dead 2\\left4dead2_dlc1\\pak01_dir.vpk");
        vpkFS2.loadAndMount(2);
        var vpkFS3 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Left 4 Dead 2\\left4dead2_dlc2\\pak01_dir.vpk");
        vpkFS3.loadAndMount(3);
        var vpkFS4 = new VPKFileSystem("H:\\Games\\Windows\\SteamLibrary\\SteamApps\\common\\Left 4 Dead 2\\left4dead2_dlc3\\pak01_dir.vpk");
        vpkFS4.loadAndMount(3);
    }

    public static void registerLoaders() {
        AssetLoader.get().registerLoader(SourceAssetTypes.VTF, new VTFLoader());
        AssetLoader.get().registerLoader(SourceAssetTypes.MDL, new StudioModelLoader());

        AssetCache.get().registerCacheProvider(SourceAssetTypes.VTF, new HyResourceCache<>());
        AssetCache.get().registerCacheProvider(SourceAssetTypes.MDL, new HyResourceCache<>());
    }
    public static void initialize(){
        registerLoaders();
        Engine.getComponentRegistry().registerComponentType(new BoneMergeComponent());
        Engine.getComponentRegistry().registerComponentType(new StudioModelComponent());
    }
}
