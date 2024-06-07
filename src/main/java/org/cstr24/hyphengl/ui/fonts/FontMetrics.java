package org.cstr24.hyphengl.ui.fonts;

public class FontMetrics {
    public int ascent;
    public int descent;
    public int lineGap;

    public FontMetrics(int asc, int desc, int lgap){
        this.ascent = asc;
        this.descent = desc;
        this.lineGap = lgap;
    }

    public float getAscent(float scale){
        return ascent * scale;
    }
    public float getAscent(){
        return getAscent(1f);
    }
    public float getDescent(float scale){
        return descent * scale;
    }
    public float getDescent(){
        return getDescent(1f);
    }
    public float getLineGap(float scale){
        return lineGap * scale;
    }
    public float getLineGap(){
        return getLineGap(1f);
    }
}
