package org.cstr24.hyphengl.ui.fonts;

import org.cstr24.hyphengl.assets.AbstractResourceLoader;
import org.cstr24.hyphengl.engine.Engine;
import org.cstr24.hyphengl.filesystem.HyFile;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FontLoader extends AbstractResourceLoader<HyFont> {
    private static final Logger LOGGER = Logger.getLogger(FontLoader.class.getName());

    private HyFont defaultFont;
    private final HashMap<String, HyFile> fontPaths;

    public FontLoader(){
        fontPaths = new HashMap<>();
    }

    public HyFont loadFont(String handle) throws IOException {
        HyFile fontFile = HyFile.get(handle);
        return loadFont(fontFile);
    }
    public HyFont loadFont(HyFile fontFile) throws IOException {
        HyFont result = null;
        if (fontFile.exists()){
            ByteBuffer fontBuffer = fontFile.getFileAsByteBuffer();
            STBTTFontinfo fontInfo = STBTTFontinfo.malloc();

            STBTruetype.stbtt_InitFont(fontInfo, fontBuffer);

            result = new HyFont(fontBuffer, fontInfo);
            result.setFontName(fetchNameTableProperty(fontInfo, HyFont.FONT_FAMILY_NAME_ID));
            result.setStyleName(fetchNameTableProperty(fontInfo, HyFont.FONT_STYLE_ID));

            result.resourceName = result.getFontName();
            result.retrievalHandle = fontFile.getPathString();

            loadToNanoVG(result);

            result.setLoaded(true);
        }

        return result;
    }
    public void loadToNanoVG(HyFont font){
        int loadResult = NanoVG.nvgCreateFontMem(Engine.getNvgContext(), font.getFontName(), font.getFontData(), false);
        font.setNVGHandle(loadResult);
    }

    @Override
    public HyFont loadResource(String handle) {
        try {
            if (fontPaths.containsKey(handle)){
                return loadFont(fontPaths.get(handle));
            }
            //assume it's a file path
            return loadFont(handle);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "A font could not be located using handle " + handle, e);
        }

        return null;
    }

    @Override
    public void preload() {
        //we're going to enumerate a fonts directory, load them all at startup, because why the hell not
        //native JVM methods because I think I need to re-think my entire filesystem abstraction setup TODO baby
        Path fontDir = Paths.get("res/fonts");
        if (Files.isDirectory(fontDir)){
            try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(fontDir)){
                dirStream.forEach(entry -> {
                    //create a map of friendly font names and paths so we can load by name

                    try (MemoryStack stack = MemoryStack.stackPush()){
                        String fontName = peekFont(entry, stack);
                        if (fontName != null){
                            fontPaths.put(fontName, HyFile.get(entry)); //only do the HyFile lookup once
                        }
                    }
                });
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Unable to pre-load font resources.", e);
            }
        }

        //load a fallback font
        defaultFont = loadResource("Open Sans");
    }

    private String peekFont(Path fontPath, MemoryStack stack){
        try {
            ByteBuffer fontBuffer = HyFile.get(fontPath).getFileAsByteBuffer();
            STBTTFontinfo fontInfo = STBTTFontinfo.malloc(stack);
            STBTruetype.stbtt_InitFont(fontInfo, fontBuffer);

            return fetchNameTableProperty(fontInfo, HyFont.FONT_FAMILY_NAME_ID);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Could not peek font " + fontPath + ".", e);
        }
        return null;
    }

    private String fetchNameTableProperty(STBTTFontinfo fontInfo, int index){
        String result = "undefined";
        ByteBuffer bb = STBTruetype.stbtt_GetFontNameString(
                fontInfo,
                STBTruetype.STBTT_PLATFORM_ID_MICROSOFT,
                STBTruetype.STBTT_MS_EID_UNICODE_BMP,
                STBTruetype.STBTT_MS_LANG_ENGLISH,
                index
        );
        if (bb != null){
            byte[] strBytes = new byte[bb.limit()];
            bb.get(strBytes);

            result = new String(strBytes, StandardCharsets.UTF_16); //this is to resolve some weirdness (at least on Windows).
        }
        return result;
    }

    @Override
    public HyFont supplyDefault() {
        return defaultFont;
    }
}
