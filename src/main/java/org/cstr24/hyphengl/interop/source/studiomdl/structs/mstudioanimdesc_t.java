package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.SourceInterop;
import org.cstr24.hyphengl.interop.source.StructWrapper;
import org.cstr24.hyphengl.interop.source.structs.BaseStruct;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class mstudioanimdesc_t extends BaseStruct implements StructWrapper<mstudioanimdesc_t> {
    public int baseptr;
    public int sznameindex;

    public float fps;
    public int flags;
    public int numframes;

    public int nummovements;
    public int movementindex;

    //skip 6

    public int animblock;
    public int animindex;

    public int numikrules;
    public int ikruleindex;
    public int animblockikruleindex;

    public int numlocalhierarchy;
    public int localhiherarchyindex;

    public int sectionindex;
    public int sectionframes;

    public short zeroframespan;
    public short zeroframecount;
    public int zeroframeindex;
    public float zeroframestalltime;

    public String animName;
    public ArrayList<mstudiomovement_t> movements;

    /** preloaded section data **/
    public ArrayList<Section> sections;

    @Override
    public mstudioanimdesc_t parse(ByteBuffer in) {
        baseptr = in.getInt();
        sznameindex = in.getInt();
        animName = SourceInterop.fetchNullTerminatedString(in, structPos + this.sznameindex, 64);

        fps = in.getFloat();
        flags = in.getInt();
        numframes = in.getInt();

        nummovements = in.getInt();
        movementindex = in.getInt();

        if (nummovements > 0){
            movements = new ArrayList<>();

            int preMovementPos = in.position();
            in.position(this.structPos + movementindex);

            for (int m = 0; m < nummovements; m++){
                movements.add(new mstudiomovement_t().parse(in));
            }

            in.position(preMovementPos);
        }

        skip(in, Integer.BYTES * 6);

        animblock = in.getInt();
        animindex = in.getInt(); //non-zero when anim data isn't in sections

        numikrules = in.getInt();
        ikruleindex = in.getInt(); //non-zero when IK data is stored in the model
        animblockikruleindex = in.getInt(); //non-zero when IK data is stored in the animblock file

        numlocalhierarchy = in.getInt();
        localhiherarchyindex = in.getInt();

        sectionindex = in.getInt();
        sectionframes = in.getInt(); //number of frames used in each fast lookup section, zero if not used

        zeroframespan = in.getShort();
        zeroframecount = in.getShort();
        zeroframeindex = in.getInt();
        zeroframestalltime = in.getFloat();

        //spit the name out:
        /*System.out.println("ANIMATION NAME: " + animName);
        System.out.println("\tNum frames: " + numframes + "  / fps: " + fps + " / flags: " + flags);
        System.out.println("\tNum movements: " + nummovements);

        if (flags != 0){
            System.out.println("\tFlag definition:");
            if ((flags & mstudioseqdesc_t.STUDIO_ALLZEROS) == mstudioseqdesc_t.STUDIO_ALLZEROS){
                System.out.println("\t\tstudio_allzeros");
            }
            if ((flags & mstudioseqdesc_t.STUDIO_DELTA) == mstudioseqdesc_t.STUDIO_DELTA){
                System.out.println("\t\tstudio_delta");
            }
            if ((flags & mstudioseqdesc_t.STUDIO_LOOPING) == mstudioseqdesc_t.STUDIO_LOOPING){
                System.out.println("\t\tstudio_looping");
            }
            if ((flags & mstudioseqdesc_t.STUDIO_SNAP) == mstudioseqdesc_t.STUDIO_SNAP){
                System.out.println("\t\tstudio_snap");
            }

        }*/

        sections = new ArrayList<>();

        return this;
    }

    @Override
    public int sizeOf() {
        return 0;
    }

    public static class Section{
        public List<mstudioanim_t> animDescs;

        public Section(){
            animDescs = new ArrayList<>();
        }
    }
}
