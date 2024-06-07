package org.cstr24.hyphengl.interop.source.vtf;

import org.cstr24.hyphengl.assets.AbstractResourceLoader;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.cstr24.hyphengl.interop.source.SourceAssetTypes;
import org.cstr24.hyphengl.textures.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11C.GL_RGB8;
import static org.lwjgl.opengl.GL11C.GL_RGBA8;

public class VTFLoader extends AbstractResourceLoader<HyTexture> {
    private static final Logger LOGGER = Logger.getLogger(VTFLoader.class.getName());

    private HyTexture defaultVTF;

    private HyTexture loadVTF(HyFile file) throws IOException {
        if (!file.exists()){
            return null;
        }

        VTFFile vtFile = null;
        HyTexture result = null;
        ByteBuffer buffer = file.mapFile().order(ByteOrder.LITTLE_ENDIAN);

        vtFile = new VTFFile().parse(buffer);

        /*System.out.println("VTF: " + file.getFileName() + " version " + vtFile.versionMajor + "." + vtFile.versionMinor +
                " | " + vtFile.width + "x" + vtFile.height);*/
        //fix-ups:
        if (vtFile.depth == 0){
            vtFile.depth = 1;
        }
        if (vtFile.isEnvironmentMap()){
            System.out.println("> is a cubemap, with faces: " + vtFile.getNumFaces());
        }
        //System.out.println("> colourspace is " + result.colourSpace);
        //System.out.println("> image format is: " + result.imageFormat);
        if (vtFile.imageFormat.compressed){
            int blockSize = vtFile.imageFormat.getCompressedBlockSize();
            //System.out.println("> block size: " + result.imageFormat.getCompressedBlockSize());
            //System.out.println("> biggest mip: " + result.imageFormat.computeCompressedMipSize(result.width, result.height));

            int largeMipOffset = vtFile.getHighResDataOffset();
            //System.out.println("> largest mip offset: " + largeMipOffset + " | mip count: " + result.mipmapCount);

            if (!vtFile.isEnvironmentMap()){
                int[] mipSizes = new int[vtFile.mipmapCount];
                int[][] mipDims = new int[vtFile.mipmapCount][2];
                int runningW = vtFile.width;
                int runningH = vtFile.height;

                for (int i = 0; i < vtFile.mipmapCount; i++){
                    int idx = vtFile.mipmapCount - 1 - i;
                    mipSizes[idx] = vtFile.imageFormat.computeCompressedMipSize(runningW, runningH);
                    mipDims[idx][0] = runningW;
                    mipDims[idx][1] = runningH;
                    //System.out.println("> mip: " + mipSizes[idx] + " : " + mipDims[idx][0] + " x " + mipDims[idx][1]);
                    runningW /= 2;
                    runningH /= 2;
                }

                TextureData info = new TextureData(file.getFileName());
                info.imageWidth = vtFile.width;
                info.imageHeight = vtFile.height;
                info.format = vtFile.imageFormat;
                info.internalFormat = (info.format.channels == 3 ? GL_RGB8 : GL_RGBA8);

                var mipDataArray = new TextureData.MipData[1][vtFile.mipmapCount];
                int runningMipOffset = largeMipOffset;

                for (int mipL = 0; mipL < vtFile.mipmapCount; mipL++){
                    var mipDataObj = new TextureData.MipData();
                    mipDataObj.width = mipDims[mipL][0];
                    mipDataObj.height = mipDims[mipL][1];
                    mipDataObj.level = (vtFile.mipmapCount - 1) - mipL;
                    //System.out.println("> processing mip: " + mipSizes[mipL] + " : " + mipDims[mipL][0] + " x " + mipDims[mipL][1] + " LEVEL " + mipDataObj.level);

                    mipDataObj.data = buffer.slice(runningMipOffset, mipSizes[mipL]).position(0).order(ByteOrder.LITTLE_ENDIAN);

                    runningMipOffset += mipSizes[mipL];

                    mipDataArray[0][mipL] = mipDataObj;
                }

                info.setMipData(mipDataArray);
                var tx2 = TextureFactory.createTex2D(info);
                tx2.setUserData(vtFile);
                tx2.setFilterModes(FilterMode.LinearMipmapLinear, FilterMode.Linear);
                tx2.setWrapModes2D(WrapMode.Repeat, WrapMode.Repeat);
                return tx2;
            }
        }else{
            //it's not compressed
            System.out.println("This is not compressed.");
            //TODO i never implemented non-compressed images
            //gman face is not compressed
            //lightwarps are not compressed - no wonder they've never worked!

            
        }

        return null;
    }

    @Override
    public HyTexture loadResource(String handle) {
        HyTexture result = null;

        try {
            result = loadVTF(HyFile.get(handle));
            if (result != null){
                result.setAssetType(SourceAssetTypes.VTF);
                result.retrievalHandle = handle;
                result.setLoaded(true);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.toString(), e);
        }

        return result;
    }

    @Override
    public void preload() {
        defaultVTF = loadResource("res/missing.vtf");
    }

    @Override
    public HyTexture supplyDefault() {
        return defaultVTF;
    }
}
