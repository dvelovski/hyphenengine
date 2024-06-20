package org.cstr24.hyphenengine.ui.fonts;

import org.cstr24.hyphenengine.assets.HyAsset;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;

import java.nio.ByteBuffer;

public class HyFont extends HyAsset {
    public static final String RESOURCE_TYPE = "Font";

    public static final int FONT_FAMILY_NAME_ID = 1;
    public static final int FONT_STYLE_ID = 2;
    public static final int FONT_SAMPLE_TEXT_ID = 19;

    private ByteBuffer fontData;
    private STBTTFontinfo fontInfo;
    private int nvgHandle = -1;

    private String fontName = "";
    private String styleName = "";

    public HyFont(){
        setAssetType(RESOURCE_TYPE);
    }
    public HyFont(ByteBuffer buffer, STBTTFontinfo stbFontInfo){
        this();
        this.fontData = buffer;
        this.fontInfo = stbFontInfo;
    }

    @Override
    public void unload() {
        fontData = null;
        fontInfo.free();
    }

    public ByteBuffer getFontData(){
        return fontData;
    }

    public String getFontName(){
        return fontName;
    }
    public void setFontName(String fName){
        this.fontName = fName;
    }
    public String getStyleName(){
        return styleName;
    }
    public void setStyleName(String sName){
        this.styleName = sName;
    }

    public void setNVGHandle(int nvg){
        this.nvgHandle = nvg;
    }
    public int getNVGHandle(){
        return nvgHandle;
    }

    public FontMetrics getFontVMetrics(){
        int[] asc = {0}, desc = {0}, lGap = {0};
        STBTruetype.stbtt_GetFontVMetrics(fontInfo, asc, desc, lGap);

        return new FontMetrics(asc[0], desc[0], lGap[0]);
    }
}
