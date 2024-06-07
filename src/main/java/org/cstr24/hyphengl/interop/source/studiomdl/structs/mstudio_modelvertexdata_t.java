package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudio_modelvertexdata_t extends BaseStruct implements StructWrapper<mstudio_modelvertexdata_t> {
    @Override
    public mstudio_modelvertexdata_t parse(ByteBuffer in) {
        //const voids - are these pointer members?
        skip(in, Integer.BYTES * 2);
        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
