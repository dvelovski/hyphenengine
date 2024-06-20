package org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class StripGroupHeader_t extends BaseStruct implements StructWrapper<StripGroupHeader_t> {
    public static final int SIZE = 25;

    public int numVerts;
    public int vertOffset;

    public int numIndicies;
    public int indexOffset;

    public int numStrips;
    public int stripOffset;

    public short flags;

    public ArrayList<StripHeader_t> stripHeaders;
    public ArrayList<Vertex_t> vertices;
    public ArrayList<Short> indices;


    @Override
    public StripGroupHeader_t parse(ByteBuffer in) {
        numVerts = in.getInt();
        vertOffset = in.getInt();

        numIndicies = in.getInt();
        indexOffset = in.getInt();

        numStrips = in.getInt();
        stripOffset = in.getInt();

        flags = uByteToShort(in.get());

        stripHeaders = new ArrayList<>();
        vertices = new ArrayList<>();
        indices = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }
}
