package org.cstr24.hyphenengine.interop.source.vbsp;

import info.ata4.io.buffer.ByteBufferInputStream;
import org.apache.commons.io.IOUtils;
import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.interop.source.SourceGame;
import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.vbsp.structs.dheader_t;
import org.cstr24.hyphenengine.interop.source.vbsp.structs.lump_t;
import org.cstr24.hyphenengine.interop.source.vbsp.structs.lump_t_l4d2;
import org.tukaani.xz.LZMAInputStream;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class BSPLoader {
    private HyFile bspFile;
    private ByteBuffer fileBuffer;
    private ByteOrder fileOrdering;

    private boolean loaded = false;
    private boolean override1 = false;

    private lump_t[] lumps = new lump_t[Constants.HEADER_LUMPS];
    private SourceGame gameType;

    private dheader_t mapHeader = new dheader_t();

    public BSPLoader(String _bspPath, SourceGame _game){
        this(HyFile.get(_bspPath), _game);
    }
    public BSPLoader(HyFile _bspFile, SourceGame _game){
        this.bspFile = _bspFile;
        this.gameType = _game;
    }
    public BSPLoader override1(){
        override1 = true;
        return this;
    }
    public boolean load() throws Exception {
        if (bspFile.exists()){
            this.fileBuffer = bspFile.mapFile().order(ByteOrder.LITTLE_ENDIAN); //order it LE so we read the magic int correctly
            int headerMagic = fileBuffer.getInt(0); //absolute get, so I don't have to reset the buffer's pos

            System.out.println("header: " + new String(SourceInterop.intToByteArray(headerMagic)));

            switch (headerMagic){
                case Constants.VBSP_HEADER -> {
                    System.out.println(bspFile.getFilePath() + " is a little-endian BSP.");
                    fileBuffer.order(ByteOrder.LITTLE_ENDIAN);
                }
                case Constants.PSBV_HEADER -> {
                    System.out.println(bspFile.getFilePath() + " is a big-endian BSP.");
                    fileBuffer.order(ByteOrder.BIG_ENDIAN);
                }
                case Constants.rBSP_HEADER -> {
                    System.out.println(bspFile.getFilePath() + " is a Respawn BSP.");
                }
                default -> throw new Exception("Error - unsupported BSP format! " + Arrays.toString(SourceInterop.intToByteArray(headerMagic)));
            }
            fileOrdering = fileBuffer.order();

            mapHeader.ident = fileBuffer.getInt();
            mapHeader.version = fileBuffer.getInt();

            boolean l4d2 = (gameType == SourceGame.Left4Dead2);
            for (int lumpNum = 0; lumpNum < Constants.HEADER_LUMPS; lumpNum++){
                if ((mapHeader.version == 21 && l4d2) || override1){
                    lumps[lumpNum] = new lump_t_l4d2();
                }else{
                    lumps[lumpNum] = new lump_t();
                }

                lumps[lumpNum].parse(fileBuffer);
                lumps[lumpNum].lumpID = lumpNum;
                //System.out.println("Lump: " + lumpNum + " fourCC: " + Arrays.toString(SourceInterop.intToByteArray(lumps[lumpNum].fourCC)));
            }

            mapHeader.mapRevision = fileBuffer.getInt();

            System.out.println(
                "Map BSP version: " + mapHeader.version + "\n" +
                "Map revision: " + mapHeader.mapRevision
            );

            loaded = true;
            return true;
        }else{
            System.out.println("File does not exist!");
            return false;
        }
    }

    public ByteBuffer getLumpBuffer(int lumpIndex) throws Exception {
        if (loaded){
            var lump = lumps[lumpIndex];

            var readBuffer = fileBuffer.slice(lump.fileOfs, lump.fileLen).order(ByteOrder.LITTLE_ENDIAN);
            //readBuffer.rewind();

            int header = readBuffer.getInt();
            //System.out.println("index " + lumpIndex + ": " + new String(SourceInterop.intToByteArray(header)));

            if (header == SourceInterop.LZMA_ID){
                System.out.println(lumpIndex + ": this lump is LZMA encoded.");

                int actualSize = readBuffer.getInt();
                int lzmaDeclaredSize = readBuffer.getInt();
                byte propertyByte = readBuffer.get();
                int dictSize = readBuffer.getInt();

                int lzmaCompressedSize = readBuffer.limit() - SourceInterop.LZMA_HEADER_SIZE;

                System.out.println("LZMA declared size: " + lzmaDeclaredSize + " vs. found size: " + lzmaCompressedSize);
                System.out.println(lump.fourCC + " - uncompressed size per fourCC - vs. LZMA actual size: " + actualSize);

                try (LZMAInputStream lzmaIn = new LZMAInputStream(new ByteBufferInputStream(readBuffer), actualSize, propertyByte, dictSize)){
                    return ByteBuffer.wrap(IOUtils.toByteArray(lzmaIn)).order(ByteOrder.LITTLE_ENDIAN);
                }
            }

            return readBuffer.rewind();
        }else{
            throw new Exception("Attempted to fetch lump data from a BSP that has not been loaded.");
        }
    }
    public lump_t getLump(int lumpIndex){
        return lumps[lumpIndex];
    }

    public <S extends StructWrapper<?>> ArrayList<S> deserializeLumpData(int lumpIndex, Class<S> type) throws Exception{
        return deserializeLumpData(lumps[lumpIndex], type);
    }
    public <S extends StructWrapper<?>> ArrayList<S> deserializeLumpData(lump_t dataLump, Class<S> type) throws Exception {
        if (loaded){
            ArrayList<S> returnList = new ArrayList<>();
            if (dataLump.fileLen == 0){
                System.out.println("There are no elements in this lump.");
            }else{
                try {
                    ByteBuffer lumpBuffer = getLumpBuffer(dataLump.lumpID);

                    Constructor<S> structConstructor = type.getConstructor();
                    int trueLumpSize = (dataLump.fourCC != 0 ? dataLump.fourCC : dataLump.fileLen);
                    int structBaseSize = structConstructor.newInstance().sizeOf();

                    while (lumpBuffer.position() < trueLumpSize) {
                        long currPos = lumpBuffer.position();

                        S elem = type.getConstructor().newInstance();
                        elem.parseStruct(lumpBuffer); //24-02-24 updated to ParseStruct so we can store structs' offsets

                        long endPos = lumpBuffer.position();

                        if (endPos - currPos != structBaseSize) {
                            System.out.println(type.getSimpleName() + " -> expected " + elem.sizeOf() + " and read " + (endPos - currPos) + " bytes instead");
                        }

                        returnList.add(elem);
                    }

                    if (returnList.size() != (trueLumpSize / structBaseSize)){
                        System.out.println("expected to read " + (trueLumpSize / structBaseSize) + " of " + type.getSimpleName() + " but read " + returnList.size() + " instead");
                    }else{
                        //System.out.println("parsed " + returnList.size() + " of " + type.getSimpleName() + " which is what was expected");
                    }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }

            return returnList;
        }else{
            throw new Exception("Attempted to fetch lump data from a BSP that has not been loaded.");
        }
    }

    public Path getBSPPath(){
        return this.bspFile.getFilePath();
    }
    public int getMapVersion(){
        return this.mapHeader.version;
    }
    public int getMapRevision(){
        return this.mapHeader.mapRevision;
    }
    public dheader_t getMapHeader(){
        return mapHeader;
    }
}
