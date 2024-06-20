package org.cstr24.hyphenengine.ui.css;

import com.helger.css.ECSSVersion;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.errorhandler.DoNothingCSSParseErrorHandler;
import org.cstr24.hyphenengine.assets.AResourceLoader;
import org.cstr24.hyphenengine.assets.AssetLoadTask;
import org.cstr24.hyphenengine.filesystem.HyFile;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HyStyleSheetLoader extends AResourceLoader<HyStyleSheet> {
    private static final Logger LOGGER = Logger.getLogger(HyStyleSheetLoader.class.getName());

    private HyStyleSheet defaultCSS;


    @Override
    public HyStyleSheet loadResource(String handle) {
        try {
            HyFile fileHandle = HyFile.get(handle);
            ByteBuffer cssBuffer = fileHandle.getFileAsByteBuffer();
            String cssString = MemoryUtil.memUTF8(cssBuffer);

            HyStyleSheet result = new HyStyleSheet();
            result.cssSheet = CSSReader.readFromString(cssString, ECSSVersion.CSS30, new DoNothingCSSParseErrorHandler());
            result.resourceName = fileHandle.getFileNameNoExt();
            result.retrievalHandle = handle;

            return result;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, e.getMessage(), e);
        }

        return null;
    }

    @Override
    public void preload() {
        defaultCSS = loadResource("res/ui/basestyles.css");
    }

    @Override
    public void unloadDefaults() {
        defaultCSS.unload();
    }

    @Override
    public HyStyleSheet supplyDefault() {
        return defaultCSS;
    }

    @Override
    public AssetLoadTask beginAssetLoad() {
        return null;
    }

    @Override
    public void mainAssetLoad(AssetLoadTask aTask) {

    }

    @Override
    public void setAssetContents(ByteBuffer source) {

    }

    @Override
    public void setAssetContents(HyStyleSheet source) {

    }
}
