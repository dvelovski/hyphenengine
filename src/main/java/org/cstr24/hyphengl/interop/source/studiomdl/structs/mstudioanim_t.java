package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioanim_t extends BaseStruct implements StructWrapper<mstudioanim_t> {
    public static final int SIZE = 4;
    public static final byte STUDIO_ANIM_RAWPOS = 1; //0x01 - Vector48
    public static final byte STUDIO_ANIM_RAWROT = 2; //0x02 - Quaternion48
    public static final byte STUDIO_ANIM_ANIMPOS = 4; //0x04 - mstudioanim_valueptr_t
    public static final byte STUDIO_ANIM_ANIMROT = 8; //0x08 - mstudioanim_valueptr_t
    public static final byte STUDIO_ANIM_DELTA = 16; //0x10
    public static final byte STUDIO_ANIM_RAWROT2 = 32; //0x20

    public byte bone;
    public byte flags;
    public short nextOffset;

    @Override
    public mstudioanim_t parse(ByteBuffer in) {
        bone = in.get();
        flags = in.get();

        nextOffset = in.getShort();

        return this;
    }

    @Override
    public int sizeOf() {
        return SIZE;
    }

    public Quaternion48 pQuat48(ByteBuffer in){
        int preQuat = in.position();
        Quaternion48 result = new Quaternion48().parseStruct(in, structPos + SIZE);
        in.position(preQuat);
        return result;
    }

    public Quaternion64 pQuat64(ByteBuffer in){
        int preQuat = in.position();
        Quaternion64 result = new Quaternion64().parseStruct(in, structPos + SIZE);
        in.position(preQuat);
        return result;
    }

    public Vector48 pPos(ByteBuffer in){
        int preVec = in.position();
        Vector48 result;

        int resultPos = this.structPos + SIZE;
        if ((flags & mstudioanim_t.STUDIO_ANIM_RAWROT) == mstudioanim_t.STUDIO_ANIM_RAWROT){
            resultPos += Quaternion48.SIZE;
        }
        if ((flags & mstudioanim_t.STUDIO_ANIM_RAWROT2) == mstudioanim_t.STUDIO_ANIM_RAWROT2){
            resultPos += Quaternion64.SIZE;
        }
        result = new Vector48().parseStruct(in, resultPos);

        in.position(preVec);
        return result;
    }

    public mstudioanim_valueptr_t pRotV(ByteBuffer in){
        int preRotV = in.position();
        mstudioanim_valueptr_t result = new mstudioanim_valueptr_t().parseStruct(in, structPos + SIZE);
        in.position(preRotV);
        return result;
    }

    public mstudioanim_valueptr_t pPosV(ByteBuffer in){
        int prePosV = in.position();

        int resultPos = this.structPos + SIZE;
        if ((flags & mstudioanim_t.STUDIO_ANIM_ANIMROT) == mstudioanim_t.STUDIO_ANIM_ANIMROT){
            resultPos += mstudioanim_valueptr_t.SIZE;
            /* I believe this is saying add sizeof mstudioanijm_valueptr_t:
            return (mstudioanim_valueptr_t *)
            (pData()) + ((flags & STUDIO_ANIM_ANIMROT) != 0)
            pData returns byte pointer but is being cast to mstudioanim_valueptr_t, which we're then incrementing that struct pointer by 1 if flags & mstudioanimrot & flags is not 0 (as this is 'true').
            */
        }

        mstudioanim_valueptr_t result = new mstudioanim_valueptr_t().parseStruct(in, resultPos);
        in.position(prePosV);
        return result;
    }

    public mstudioanim_t next(ByteBuffer in) {
        if (nextOffset != 0){
            return new mstudioanim_t().parseStruct(in, this.structPos + nextOffset);
        }
        return null;
    }
}
