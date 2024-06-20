package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioanim_valueptr_t extends BaseStruct implements StructWrapper<mstudioanim_valueptr_t> {
    public static final int SIZE = 6;
    public int[] offset;
    @Override
    public mstudioanim_valueptr_t parse(ByteBuffer in) {
        offset = new int[3];
        offset[0] = uShortToInt(in.getShort());
        offset[1] = uShortToInt(in.getShort());
        offset[2] = uShortToInt(in.getShort());

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }

    public int pAnimValue(int i){
        return this.structPos + offset[i];
        /*int prePos = in.position();
        if (offset[i] > 0){
            var result = new mstudioanimvalue_t().parseStruct(in, this.structPos + offset[i]);
            //(mstudioanimvalue_t *)
            // (
            //  ((byte *)this) + offset[i]
            // )
            in.position(prePos);
            return result;
        }else{
            return null;
        }*/
    }
}
