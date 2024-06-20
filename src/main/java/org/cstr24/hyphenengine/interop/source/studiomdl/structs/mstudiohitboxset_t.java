package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class mstudiohitboxset_t extends BaseStruct implements StructWrapper<mstudiohitboxset_t> {
    public int sznameindex;
    public int numhitboxes;
    public int hitboxindex;

    public ArrayList<mstudiobbox_t> hitboxes;

    @Override
    public mstudiohitboxset_t parse(ByteBuffer in) {
        //this.setStructOffset(in.position());
        //System.out.println("StructOffset: " + structOffset);
        //System.out.println("in.position() " + in.position());

        sznameindex = in.getInt();
        numhitboxes = in.getInt();
        hitboxindex = in.getInt();

        hitboxes = new ArrayList<>();

        //System.out.println("HITBOX SET NAME: " + SourceInterop.fetchNullTerminatedString(in, this.structPos + this.sznameindex, 64));
        //System.out.println("Number of hitboxes: " + numhitboxes);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }
}
