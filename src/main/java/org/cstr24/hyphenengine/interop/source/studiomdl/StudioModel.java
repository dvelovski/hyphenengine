package org.cstr24.hyphenengine.interop.source.studiomdl;

import org.cstr24.hyphenengine.filesystem.HyFile;
import org.cstr24.hyphenengine.geometry.HyModel;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.*;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vtx.VTXFile;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.vvd.VVDFile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

public class StudioModel extends HyModel {
    public static final int MAX_NUM_LODS = 8;
    public static final int MAX_NUM_BONES_PER_VERT = 3;
    public static final int CURRENT_STUDIO_VERSION = 49;
    public static final int MAX_STUDIO_BONES = 128;
    public static final int MAX_STUDIO_POSE_PARAM = 24;
    public static final int MAX_OVERLAYS = 15;

    public static final float FLT_EPSILON = 0.00000011920929f;;

    public HyFile fileRef;

    public studiohdr_t header;
    public studiohdr2_t header2;

    public ArrayList<mstudiobone_t> bones;
    public ArrayList<StudioModel> importedFiles;

    public ArrayList<mstudioattachment_t> attachments;
    public ArrayList<mstudiotexture_t> mStudioTextureTs;
    public ArrayList<mstudiomodelgroup_t> externalModelFiles;
    public ArrayList<mstudiomodel_t> models;

    public ArrayList<mstudioanimdesc_t> animations;
    public ArrayList<mstudioseqdesc_t> sequences;
    public ArrayList<mstudioposeparamdesc_t> poseParameters;

    public ArrayList<mstudiobodyparts_t> bodyParts;
    public ArrayList<String> modelTextureDirectories;

    public short[] skinRefs;
    public StudioModel parentFile;
    public ByteBuffer reader;
    public VVDFile vvd;
    public VTXFile vtx;

    public StudioModel(studiohdr_t hdr) {
        this(hdr, null);
    }
    public StudioModel(studiohdr_t hdr, studiohdr2_t hdr2){
        this.header = hdr;
        this.header2 = hdr2;

        this.bones = new ArrayList<>();
        this.hyBones = new ArrayList<>();

        this.attachments = new ArrayList<>();
        this.mStudioTextureTs = new ArrayList<>();
        this.models = new ArrayList<>();
        this.externalModelFiles = new ArrayList<>();
        this.animations = new ArrayList<>();
        this.sequences = new ArrayList<>();
        this.poseParameters = new ArrayList<>();
        this.bodyParts = new ArrayList<>();
        this.modelTextureDirectories = new ArrayList<>();

        importedFiles = new ArrayList<>();
    }

    public mstudioseqdesc_t getSequenceByName(String sequenceName){
        for (mstudioseqdesc_t seq : sequences){
            if ((seq.flags & mstudioseqdesc_t.STUDIO_OVERRIDE) != mstudioseqdesc_t.STUDIO_OVERRIDE && seq.sequenceName.equals(sequenceName)){
                return seq;
            }
        }
        for (StudioModel inclFile : importedFiles){
            mstudioseqdesc_t result = inclFile.getSequenceByName(sequenceName);
            if (result != null){
                return result;
            }
        }
        return null;
    }
    public mstudioseqdesc_t getSequence(int num){
        if (num < sequences.size()){
            var seq = sequences.get(num);
            if ((seq.flags & mstudioseqdesc_t.STUDIO_OVERRIDE) != mstudioseqdesc_t.STUDIO_OVERRIDE){
                return seq;
            }
        }
        for (StudioModel inclFile : importedFiles){
            mstudioseqdesc_t result = inclFile.getSequence(num);
            if (result != null){
                return result;
            }
        }
        return null;
    }

    public void importFile(StudioModel includedModel) {
        if (includedModel != null){
            importedFiles.add(includedModel);
            includedModel.parentFile = this;
        }
    }

    public void studio_seqAnims(mstudioseqdesc_t sequence, float[] poseParam, mstudioanimdesc_t[] pAnim, float[] weight){
        int[] i0 = {0}, i1 = {0};
        float[] s0 = {0}, s1 = {0};

        localPoseParameter(sequence, poseParam, 0, s0, i0);
        localPoseParameter(sequence, poseParam, 1, s1, i1);

        pAnim[0] = animations.get(sequence.anim(i0[0], i1[0]));
        weight[0] = (1 - s0[0]) * (1 - s1[0]);

        pAnim[1] = animations.get(sequence.anim(i0[0] + 1, i1[0]));
        weight[1] = (s0[0]) * (1 - s1[0]);

        pAnim[2] = animations.get(sequence.anim(i0[0], i1[0] + 1));
        weight[2] = (1 - s0[0]) * (s1[0]);

        pAnim[3] = animations.get(sequence.anim(i0[0] + 1, i1[0] + 1));
        weight[3] = (s0[0]) * (s1[0]);
    }

    public void localPoseParameter(mstudioseqdesc_t sequence, float[] poseParameter, int iLocalIndex, float[] flSetting, int[] index){
        int iPose = getSharedPoseParameter(sequence.id, sequence.paramindex[iLocalIndex]);

        if (iPose == -1){
            flSetting[0] = 0;
            index[0] = 0;
            return;
        }

        mstudioposeparamdesc_t pose = pPoseParameter(iPose);
        float flValue = poseParameter[iPose];

        if (pose.loop > 0){
            float wrap = (pose.start + pose.end) / 2.0f + pose.loop / 2.0f;
            float shift = pose.loop - wrap;
            flValue = (float) (flValue - pose.loop * Math.floor((flValue + shift) / pose.loop));
        }

        if (sequence.posekeyindex == 0){
            float flLocalStart = (sequence.paramstart[iLocalIndex] - pose.start) / (pose.end - pose.start);
            float flLocalEnd = (sequence.paramend[iLocalIndex] - pose.start) / (pose.end - pose.start);

            flSetting[0] = (flValue - flLocalStart) / (flLocalEnd - flLocalStart);

            if (flSetting[0] < 0){
                flSetting[0] = 0;
            }
            if (flSetting[0] > 1){
                flSetting[0] = 1;
            }

            index[0] = 0;
            if (sequence.groupsize[iLocalIndex] > 2){
                index[0] = (int) (flSetting[0] * (sequence.groupsize[iLocalIndex] - 1));
                if (index[0] == sequence.groupsize[iLocalIndex] - 1){
                    index[0] = sequence.groupsize[iLocalIndex] - 2;
                }
                flSetting[0] = flSetting[0] * (sequence.groupsize[iLocalIndex] - 1) - index[0];
            }
        }else{
            flValue = flValue * (pose.end - pose.start) + pose.start;
            index[0] = 0;

            while (true){
                flSetting[0] = (flValue - sequence.poseKey(iLocalIndex, index[0])) / (sequence.poseKey(iLocalIndex, index[0] + 1) - sequence.poseKey(iLocalIndex, index[0]));
                //flSetting[0] = (flValue - sequence.pose)
                if (index[0] < sequence.groupsize[iLocalIndex] - 2 && flSetting[0] > 1.0){
                    index[0]++;
                    continue;
                }
                break;
            }

            flSetting[0] = Math.max(0.0f, Math.min(1.0f, flSetting[0]));
        }
    }

    public void studioLocalPoseParameter(float[] poseParameter, mstudioseqdesc_t sequence, int iLocalIndex, float[] flSetting, int[] index){
        int iPose = getSharedPoseParameter(sequence.id, sequence.paramindex[iLocalIndex]);
        if (iPose == -1){
            flSetting[0] = 0;
            index[0] = 0;
            return;
        }
        mstudioposeparamdesc_t Pose = pPoseParameter(iPose);
        float flValue = poseParameter[iPose];

        if (Pose.loop != 0){
            float wrap = (Pose.start + Pose.end) / 2.0f + Pose.loop / 2.0f;
            float shift = Pose.loop - wrap;

            flValue = (float) (flValue - Pose.loop * Math.floor((flValue + shift) / Pose.loop));
        }

        if (sequence.posekeyindex == 0){
            float flLocalStart = (sequence.paramstart[iLocalIndex] - Pose.start) / (Pose.end - Pose.start);
            float flLocalEnd = (sequence.paramend[iLocalIndex] - Pose.start) / (Pose.end - Pose.start);

            flSetting[0] = (flValue - flLocalStart) / (flLocalEnd - flLocalStart);
            flSetting[0] = Math.max(0, Math.min(1, flSetting[0]));

            index[0] = 0;
            if (sequence.groupsize[iLocalIndex] > 2){
                index[0] = (int) (flSetting[0] * (sequence.groupsize[iLocalIndex] - 1));
                if (index[0] == sequence.groupsize[iLocalIndex] - 1){
                    index[0] = sequence.groupsize[iLocalIndex] - 2;
                }
                flSetting[0] = flSetting[0] * (sequence.groupsize[iLocalIndex] - 1) - index[0];
            }
        }else{
            flValue = flValue + (Pose.end - Pose.start) + Pose.start;
            index[0] = 0;

            while (true){
                flSetting[0] = (flValue -
                        sequence.poseKey(iLocalIndex, index[0])) /
                        (sequence.poseKey(iLocalIndex, index[0])) -
                        (sequence.poseKey(iLocalIndex, index[0]));

                if (index[0] < sequence.groupsize[iLocalIndex] - 2 && flSetting[0] > 1.0f){
                    index[0]++;
                    continue;
                }
                break;
            }
        }

        flSetting[0] = Math.max(0, Math.min(1, flSetting[0]));
    }

    public mstudioposeparamdesc_t pPoseParameter(int i){
        //for now just implementing local pose parameters
        return poseParameters.get(i);
    }

    public int getSharedPoseParameter(int iSequence, int iLocalPose){
        if (iLocalPose == -1){
            return iLocalPose;
        }
        return iLocalPose;
    }

    public mstudioanimblock_t pAnimBlock(int i){
        return new mstudioanimblock_t().parseStruct(reader, header.structPos + header.animblockindex + (mstudioanimblock_t.SIZE * i));
    }

    public mstudioanimsections_t pSection(mstudioanimdesc_t animation, int i){
        return new mstudioanimsections_t().parseStruct(reader, animation.structPos + animation.sectionindex + (mstudioanimsections_t.SIZE * i));
    }

    public int getSharedPoseParameter(mstudioseqdesc_t sequence, int iLocalPose){
        return iLocalPose;
    }

    public int iRelativeAnim(mstudioseqdesc_t sequence, int relAnim){
        return relAnim;
    }

    public int iRelativeSequence(mstudioseqdesc_t baseSequence, int relativeSequence){
        return relativeSequence;
    }

    public mstudioanim_t pAnim(mstudioanimdesc_t animation, int[] piFrame){
        if (animation.sectionframes != 0){
            int section = piFrame[0] / animation.sectionframes;
            if (animation.numframes > animation.sectionframes && piFrame[0] == animation.numframes - 1){
                piFrame[0] = 0;
                section = (animation.numframes / animation.sectionframes) + 1;
            }
            int offset = animation.structPos + animation.sectionindex + mstudioanimsections_t.SIZE * section;
            var sec = new mstudioanimsections_t().parseStruct(reader, offset);
            if (sec.animblock == 0){
                return new mstudioanim_t().parseStruct(reader, animation.structPos + sec.animindex);
            }
        }else{
            return new mstudioanim_t().parseStruct(reader, animation.structPos + animation.animindex);
        }
        return null;
    }

    public int boneFlags(int boneIndex){
        return bones.get(boneIndex).flags;
    }

    public mstudioseqdesc_t getSuitableSequence(){
        //this means it does NOT start with 'layer', and does NOT have the override flag
        mstudioseqdesc_t sqRef = null;
        boolean suitable = false;

        while (!suitable){
            sqRef = getSequence(ThreadLocalRandom.current().nextInt(sequences.size()));
            suitable = !sqRef.sequenceName.startsWith("layer") && ((sqRef.flags & mstudioseqdesc_t.STUDIO_DELTA) != mstudioseqdesc_t.STUDIO_DELTA);
        }

        //System.out.println(sqRef);

        return sqRef;
    }
}
