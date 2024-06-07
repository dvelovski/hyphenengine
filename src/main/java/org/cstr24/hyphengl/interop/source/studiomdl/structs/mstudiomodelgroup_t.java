package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudiomodelgroup_t extends BaseStruct implements StructWrapper<mstudiomodelgroup_t> {
    public int szlabelindex;
    public int sznameindex;

    public String textualName = "";
    public String filename = "";

    @Override
    public mstudiomodelgroup_t parse(ByteBuffer in) {
        szlabelindex = in.getInt();
        sznameindex = in.getInt();

        textualName = SourceInterop.fetchNullTerminatedString(in, structPos + this.szlabelindex, 64);
        //System.out.println("TEXTUAL NAME: " + textualName);

        filename = SourceInterop.fetchNullTerminatedString(in, structPos + this.sznameindex, 64);
        //System.out.println("FILE NAME: " + filename);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
