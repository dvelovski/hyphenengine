package org.cstr24.hyphenengine.interop.source.studiomdl.structs;

import org.cstr24.hyphenengine.interop.source.SourceInterop;
import org.cstr24.hyphenengine.interop.source.StructWrapper;
import org.cstr24.hyphenengine.interop.source.structs.BaseStruct;
import org.cstr24.hyphenengine.interop.source.structs.vector_t;
import org.cstr24.hyphenengine.interop.source.studiomdl.StudioModel;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class mstudioseqdesc_t extends BaseStruct implements StructWrapper<mstudioseqdesc_t> {
    //ending frame should be the same as the starting flame
    public static final int STUDIO_LOOPING = 0x0001;

    //do not interpolate between previous animation and this one
    public static final int STUDIO_SNAP = 0x0002;

    //this sequence "adds" to the base sequences, not slerp blends
    public static final int STUDIO_DELTA = 4;

    //temporary flag that forces the sequence to always play
    public static final int STUDIO_AUTOPLAY = 0x0008;
    public static final int STUDIO_POST = 16;

    //this animation/sequence has no real animation data
    public static final int STUDIO_ALLZEROS = 0x0020;

    //0x0040 excluded

    //cycle index is taken from a pose parameter index
    public static final int STUDIO_CYCLEPOSE = 0x0080;

    //cycle index is taken from a real-time clock, not the animation cycle index
    public static final int STUDIO_REALTIME = 256;

    //sequence has a local context sequence
    public static final int STUDIO_LOCAL = 512;

    //don't show in default selection views
    public static final int STUDIO_HIDDEN = 1024;

    //a forward declared sequence (empty)
    public static final int STUDIO_OVERRIDE = 2048;

    //has been updated at runtime to activity index
    public static final int STUDIO_ACTIVITY = 0x1000;

    //has been updated at runtime to activity index
    public static final int STUDIO_EVENT = 0x2000;

    //sequence blends in worldspace
    public static final int STUDIO_WORLD = 16384;

    public float cachedCycleRate = -1f;

    public int baseptr;

    public int szlabelindex;
    public int szactivitynameindex;

    public int flags;

    public int activity;
    public int actweight;

    public int numevents;
    public int eventindex;

    public vector_t bbmin;
    public vector_t bbmax;

    public int numblends;

    /**
     * Index into array of shorts which is groupsize[0] x groupsize[1] in length.
     */
    public int animindexindex;

    public int movementindex;

    public int[] groupsize;
    public int[] paramindex;

    public float[] paramstart;
    public float[] paramend;
    public int paramparent;

    public float fadeintime; //ideal fade in time (0.2 default)
    public float fadeouttime; //ideal cross fade out time (0.2 default)

    public int localentrynode;
    public int localexitnode;
    public int nodeflags;

    public float entryphase;
    public float exitphase;

    public float lastframe;

    public int nextseq;
    public int pose;

    public int numikrules;

    public int numautolayers;
    public int autolayerindex;

    public int weightlistindex;

    public int posekeyindex;

    public int numiklocks;
    public int iklockindex;

    public int keyvalueindex;
    public int keyvaluesize;

    public int cycleposeindex;

    public String sequenceName;
    public String activityName;

    public ArrayList<mstudioevent_t> events;

    public String flagString;

    public ArrayList<mstudioautolayer_t> autoLayers;
    public ArrayList<Float> boneWeights;
    public StudioModel file;
    //ordinal number at which this seqdesc was encountered within its parent file
    public int id;

    private int[] blendArray;

    @Override
    public mstudioseqdesc_t parse(ByteBuffer in) {
        baseptr = in.getInt();

        szlabelindex = in.getInt();
        sequenceName = SourceInterop.fetchNullTerminatedString(in, structPos + szlabelindex, 64);

        szactivitynameindex = in.getInt();
        activityName = SourceInterop.fetchNullTerminatedString(in, structPos + szactivitynameindex, 64);

        flags = in.getInt();
        //System.out.println("Base ptr: " + baseptr + " vs structoffset: " + structOffset + " and flags are " + flags);

        activity = in.getInt();
        actweight = in.getInt();

        //System.out.println("Activity integer: " + activity + " / activity weight: " + actweight);

        numevents = in.getInt();
        eventindex = in.getInt();

        //System.out.println("Sequence has " + numevents + " events.");
        bbmin = new vector_t().parseStruct(in);
        bbmax = new vector_t().parseStruct(in);

        numblends = in.getInt();

        animindexindex = in.getInt();

        movementindex = in.getInt();

        groupsize = new int[]{
                in.getInt(),
                in.getInt()
        };

        //System.out.println("group size: " + groupsize[0] + " x " + groupsize[1]);

        int bufferPosition = in.position();
        blendArray = new int[groupsize[0] * groupsize[1]];
        for (int x = 0; x < groupsize[0]; x++){
            for (int y = 0; y < groupsize[1]; y++){
                int offset = y * groupsize[0] + x;
                int blendsPosition = structPos + animindexindex + (Short.BYTES * offset);

                in.position(blendsPosition);
                int value = in.getShort();
                blendArray[offset] = value;

                //System.out.println("anim value for x: " + x + ", y: " + y + " is " + value);
            }
        }
        in.position(bufferPosition);

        paramindex = new int[]{
                in.getInt(),
                in.getInt()
        };

        paramstart = new float[]{
                in.getFloat(),
                in.getFloat()
        };

        paramend = new float[]{
                in.getFloat(),
                in.getFloat()
        };

        paramparent = in.getInt();

        fadeintime = in.getFloat();
        fadeouttime = in.getFloat();

        localentrynode = in.getInt();
        localexitnode = in.getInt();
        nodeflags = in.getInt();

        entryphase = in.getFloat();
        exitphase = in.getFloat();

        lastframe = in.getFloat();

        nextseq = in.getInt();
        pose = in.getInt();

        numikrules = in.getInt();

        numautolayers = in.getInt();
        autolayerindex = in.getInt();


        weightlistindex = in.getInt();

        posekeyindex = in.getInt();

        numiklocks = in.getInt();
        iklockindex = in.getInt();

        keyvalueindex = in.getInt();
        keyvaluesize = in.getInt();

        /*if (keyvaluesize > 0){
            System.out.println("K/V val\n" + SourceInterop.fetchNullTerminatedString(in, this.structPos + keyvalueindex, 256));
        }*/

        cycleposeindex = in.getInt();

        skip(in, Integer.BYTES * 7);

        int returnPos = in.position();

        flagString = composeFlags();

        in.position(this.structPos + this.weightlistindex);
        boneWeights = new ArrayList<>();
        for (int i = 0; i < StudioModel.MAX_STUDIO_BONES; i++){ //as far as i can tell there's no way to ascertain how many boneweights there are. so this arraylist might get filled up with some rubbish and waste a bit of memory.
            if (in.position() >= in.limit()){
                break;
            }
            boneWeights.add(in.getFloat());
        }

        if (numautolayers > 0){
            autoLayers = new ArrayList<>();
            int preLayers = in.position();
            in.position(structPos + autolayerindex);
            for (int i = 0; i < numautolayers; i++){
                autoLayers.add(new mstudioautolayer_t().parseStruct(in));
            }
        }

        if (numevents > 0){
            events = new ArrayList<>();
            in.position(structPos + eventindex);
            for (int i = 0; i < numevents; i++){
                events.add(new mstudioevent_t().parseStruct(in));
            }
        }

        in.position(returnPos);

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }

    public String composeFlags(){
        StringBuilder flagBuilder = new StringBuilder();
        flagBuilder.append("Sequence name: ").append(this.sequenceName).append(" | Activity name: " ).append(this.activityName);
        if ((flags & STUDIO_LOOPING) == STUDIO_LOOPING){
            flagBuilder.append("\tSequence is looping: true");
        }
        if ((flags & STUDIO_SNAP) == STUDIO_SNAP){
            flagBuilder.append("\tSequence is Studio_Snap: true");
        }
        if ((flags & STUDIO_DELTA) == STUDIO_DELTA){
            flagBuilder.append("\tSequence is Studio_Delta: true");
        }
        if ((flags & STUDIO_AUTOPLAY) == STUDIO_AUTOPLAY){
            flagBuilder.append("\tSequence is set to autoplay: true");
        }
        if ((flags & STUDIO_POST) == STUDIO_POST){
            flagBuilder.append("\tSequence is Studio_Post: true");
        }
        if ((flags & STUDIO_ALLZEROS) == STUDIO_ALLZEROS){
            flagBuilder.append("\tSequence is allzero (has no real animation data): true");
        }
        if ((flags & STUDIO_CYCLEPOSE) == STUDIO_CYCLEPOSE){
            flagBuilder.append("\tSequence is Cycle_Pose: true");
        }
        if ((flags & STUDIO_REALTIME) == STUDIO_REALTIME){
            flagBuilder.append("\tSequence is Realtime: true");
        }
        if ((flags & STUDIO_LOCAL) == STUDIO_LOCAL){
            flagBuilder.append("\tSequence is local: true");
        }
        if ((flags & STUDIO_HIDDEN) == STUDIO_HIDDEN){
            flagBuilder.append("\tSequence is hidden: true");
        }
        if ((flags & STUDIO_OVERRIDE) == STUDIO_OVERRIDE){
            flagBuilder.append("\tThis is a forward declared sequence: true");
        }
        if ((flags & STUDIO_ACTIVITY) == STUDIO_ACTIVITY){
            flagBuilder.append("\tThis is a studio activity: true");
        }
        if ((flags & STUDIO_EVENT) == STUDIO_EVENT){
            flagBuilder.append("Studio event flag: true");
        }
        if ((flags & STUDIO_WORLD) == STUDIO_WORLD){
            flagBuilder.append("Studio world flag: true");
        }
        return flagBuilder.toString();
    }

    public int anim(int x, int y){
        if (x >= groupsize[0]){
            x = groupsize[0] - 1;
        }
        if (y >= groupsize[1]){
            y = groupsize[1] - 1;
        }
        int offset = y * groupsize[0] + x;
        if (offset < 0){
            return 0;
        }
        return blendArray[offset];
    }

    public float poseKey(int iParam, int iAnim) {
        var in = file.reader;

        int prePoseKeyPos = in.position();

        in.position((this.structPos + posekeyindex) + ((iParam * groupsize[0] + iAnim) * Float.SIZE)); //TODO may need to multiply iParam onwards by Float.SIZE bytes.
        //this is because of this cast:
        //(float *)(((byte *)this) + posekeyindex) + iParam * groupsize[0] + iAnim;
        //(this + posekeyindex) is a byte pointer but it's in parentheses, then iParam + groupsize[0] + iAnim...? i'm not sure that groupsize[0] is a size in bytes or a count of floats? it seems to be used like a dimension, not a 'size'

        float result = in.getFloat();

        in.position(prePoseKeyPos);
        return result;
    }
}
