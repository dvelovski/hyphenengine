package org.cstr24.hyphengl.ui.datatypes;

import org.lwjgl.nanovg.NVGColor;

import java.nio.ByteBuffer;

public class NVGColorX extends NVGColor {
    protected NVGColorX(long address, ByteBuffer container) {
        super(address, container);
    }
    public NVGColor rgba(float rVal, float gVal, float bVal, float aVal){
        return r(rVal).g(gVal).b(bVal).a(aVal);
    }

}
