package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioboneflexdrivercontrol_t extends BaseStruct implements StructWrapper<mstudioboneflexdrivercontrol_t> {
    private static final int SIZE = 16;

    public int boneComponent; //bone component that drives flex, StudioBoneFlexComponent_T
    public int flexControllerIndex;
    public float flMin; //min value of bone component mapped to 0 on flex controller
    public float flMax; //max value of bone component mapped to 1 on flex controller

    @Override
    public mstudioboneflexdrivercontrol_t parse(ByteBuffer in) {
        boneComponent = in.getInt();
        flexControllerIndex = in.getInt();
        flMin = in.getFloat();
        flMax = in.getFloat();
        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
