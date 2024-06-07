package org.cstr24.hyphengl.ui.datatypes;

import org.cstr24.hyphengl.rendering.Colour;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import static org.lwjgl.system.MemoryUtil.nmemFree;

public class ColourFill extends Fill{
    final FillType fillType = FillType.Colour;
    public NVGColor fillColour;

    public ColourFill(){
        fillColour = NVGColor.calloc();
    }

    public ColourFill setColour(float r, float g, float b, float a){
        fillColour.r(r).g(g).b(b).a(a);
        return this;
    }
    public ColourFill setColour(float[] rgba){
        return setColour(rgba[0], rgba[1], rgba[2], rgba[3]);
    }
    public ColourFill setColour(Colour val){
        return setColour(val.r, val.g, val.b, val.a);
    }

    @Override
    public void discard() {
        //nmemFree(fillColour.address());
    }

    @Override
    public void apply(long ctx) {
        NanoVG.nvgFillColor(ctx, fillColour);
    }
}
