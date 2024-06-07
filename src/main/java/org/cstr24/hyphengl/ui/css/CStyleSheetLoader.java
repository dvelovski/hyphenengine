package org.cstr24.hyphengl.ui.css;

import com.helger.css.ECSSVersion;
import com.helger.css.reader.CSSReader;
import com.helger.css.reader.errorhandler.DoNothingCSSParseErrorHandler;
import org.cstr24.hyphengl.assets.AbstractResourceLoader;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CStyleSheetLoader extends AbstractResourceLoader<HyCStyleSheet> {
    private static final Logger LOGGER = Logger.getLogger(CStyleSheetLoader.class.getName());

    private HyCStyleSheet defaultCSS;


    @Override
    public HyCStyleSheet loadResource(String handle) {
        try {
            HyFile fileHandle = HyFile.get(handle);
            ByteBuffer cssBuffer = fileHandle.getFileAsByteBuffer();
            String cssString = MemoryUtil.memUTF8(cssBuffer);

            HyCStyleSheet result = new HyCStyleSheet();
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
    public HyCStyleSheet supplyDefault() {
        return defaultCSS;
    }
}
