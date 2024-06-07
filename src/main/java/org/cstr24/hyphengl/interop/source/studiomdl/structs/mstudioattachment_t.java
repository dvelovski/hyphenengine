package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;

import java.nio.ByteBuffer;

public class mstudioattachment_t extends BaseStruct implements StructWrapper<mstudioattachment_t> {
    public int sznameindex;
    public long flags;
    public int localbone;

    public Matrix4f local; //attachment point
    public String name = "";

    @Override
    public mstudioattachment_t parse(ByteBuffer in) {
        int structPosition = in.position();

        sznameindex = in.getInt();
        flags = uIntToLong(in.getInt());
        localbone = in.getInt();
        local = SourceInterop.readMatrix4x3(in);

        name = SourceInterop.fetchNullTerminatedString(in, structPosition + sznameindex, 64);

        //sznameindex is given as an offset into the MDL relative to the start of the mstudioattachment_t struct, so we need to know where the struct sits within the file too.
        //System.out.println("attachment name: " + name);

        skip(in, Integer.BYTES * 8); //skip unused[8]
        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
