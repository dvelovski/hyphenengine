package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;

public class mstudioautolayer_t extends BaseStruct implements StructWrapper<mstudioautolayer_t> {
    public static final int STUDIO_AL_POST = 0x0010;

    //convert layer ramp in/out curve is a spline instead of linear
    public static final int STUDIO_AL_SPLINE = 0x0040;

    //pre-bias the ramp curve to compense for a non-1 weight, assuming a second layer is also going to accumulate
    public static final int STUDIO_AL_XFADE = 128;

    //animation always blends at 1.0 (ignores weight)
    public static final int STUDIO_AL_NOBLEND = 512;

    //layer is a local context sequence
    public static final int STUDIO_AL_LOCAL = 4096;

    //layer blends using a pose parameter instead of parent cycle
    public static final int STUDIO_AL_POSE = 16384;

    public short iSequence;
    public short iPose;

    public int flags;
    public float start;
    public float peak;
    public float tail;
    public float end;

    public String flagString;

    @Override
    public mstudioautolayer_t parse(ByteBuffer in) {
        iSequence = in.getShort();
        iPose = in.getShort();

        flags = in.getInt();
        start = in.getFloat();
        peak = in.getFloat();
        tail = in.getFloat();
        end = in.getFloat();

        flagString = composeFlags();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }

    public String composeFlags(){
        StringBuilder flagBuilder = new StringBuilder();

        if ((flags & STUDIO_AL_POST) == STUDIO_AL_POST){
            flagBuilder.append("\tAutolayer is Al_Post: true");
        }
        if ((flags & STUDIO_AL_SPLINE) == STUDIO_AL_SPLINE){
            flagBuilder.append("\tAutolayer is Al_Spline: true");
        }
        if ((flags & STUDIO_AL_XFADE) == STUDIO_AL_XFADE){
            flagBuilder.append("\tAutolayer is Al_XFade: true");
        }
        if ((flags & STUDIO_AL_NOBLEND) == STUDIO_AL_NOBLEND){
            flagBuilder.append("\tAutolayer is Noblend: true");
        }
        if ((flags & STUDIO_AL_LOCAL) == STUDIO_AL_LOCAL){
            flagBuilder.append("\tLayer is a local context sequence: true");
        }
        if ((flags & STUDIO_AL_POSE) == STUDIO_AL_POSE){
            flagBuilder.append("\tLayer is Al_Pose: true");
        }

        return flagBuilder.toString();
    }
}
