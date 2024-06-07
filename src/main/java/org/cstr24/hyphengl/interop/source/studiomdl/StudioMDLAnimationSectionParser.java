package org.cstr24.hyphengl.interop.source.studiomdl;

import org.cstr24.hyphengl.interop.source.studiomdl.structs.*;

import java.util.ArrayList;
import java.util.List;

public class StudioMDLAnimationSectionParser {
   public List<mstudioanim_t> parseAnimSection(StudioModel file, mstudioanimdesc_t animation, int section){
        var in = file.reader;

        int originalPos = in.position();
        ArrayList<mstudioanim_t> dest = new ArrayList<>();

        if (animation.sectionframes == 0){
            if (animation.animblock == 0 && animation.animindex != 0){
                mstudioanim_t anim;
                int offset = 0;
                do {
                    anim = new mstudioanim_t().parseStruct(in, animation.structPos + animation.animindex + offset);
                    dest.add(anim);
                    offset += anim.nextOffset;
                } while (anim.nextOffset != 0);
            }

            in.position(originalPos);
            return dest;
        }else{
            int sectionIndex = (int) Math.floor((double) section / animation.sectionframes); //figure out the section we're in

            int sectionOffset = animation.structPos + animation.sectionindex + (mstudioanimsections_t.SIZE * sectionIndex);

            var animSectionDesc = new mstudioanimsections_t().parseStruct(in, sectionOffset);
            if (animSectionDesc.animblock == 0){
                mstudioanim_t anim;
                int offset = 0;
                do {
                    anim = new mstudioanim_t().parseStruct(in, animation.structPos + animSectionDesc.animindex + offset);
                    offset += anim.nextOffset;
                } while (anim.nextOffset != 0);
            }
            in.position(originalPos);
            return dest;
        }
    }


}
