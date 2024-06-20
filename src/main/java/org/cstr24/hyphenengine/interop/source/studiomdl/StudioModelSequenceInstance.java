package org.cstr24.hyphenengine.interop.source.studiomdl;

import org.cstr24.hyphenengine.animation.AnimationInstance;
import org.cstr24.hyphenengine.core.Engine;
import org.cstr24.hyphenengine.entities.components.BoneMergeComponent;
import org.cstr24.hyphenengine.interop.source.SourceMath;
import org.cstr24.hyphenengine.interop.source.studiomdl.structs.*;
import org.joml.*;

import java.lang.Math;
import java.util.stream.Stream;

import static org.cstr24.hyphenengine.interop.source.studiomdl.structs.mstudioautolayer_t.*;
import static org.cstr24.hyphenengine.interop.source.studiomdl.structs.mstudioautolayer_t.STUDIO_AL_NOBLEND;
import static org.cstr24.hyphenengine.interop.source.studiomdl.structs.mstudioseqdesc_t.STUDIO_LOCAL;

public class StudioModelSequenceInstance extends AnimationInstance {
    public static float MAX_ANIMTIME_INTERVAL = 0.2f;
    public static float INTERVAL_MINIMUM = 0.001f;

    //what do I need to know here...
    public mstudioseqdesc_t sequence; //I'm calling it an animation because the source 'sequence' is really their 'animation' in the sense that I think of it. Source's line of thinking is like that as well, but they still chose dumb ass naming.
    public float cycle;

    public float previousAnimationTime;
    public float animationTime;

    public float playbackRate = 1.0f;

    public boolean sequenceFinished = false;

    public float[] poseParameter;

    public StudioModelSequenceInstance() {
        poseParameter = new float[0];
        reset();
    }
    public StudioModelSequenceInstance(mstudioseqdesc_t seq){
        this();
        this.setSequence(seq);
    }

    public void studioFrameAdvance(){
        float flInterval = (float) Engine.currentTime() - animationTime;
        flInterval = Math.max(0.0f, Math.min(flInterval, MAX_ANIMTIME_INTERVAL));

        if (flInterval <= INTERVAL_MINIMUM){
            return;
        }

        previousAnimationTime = animationTime;
        animationTime = (float) Engine.currentTime();

        float flCycleRate = getSequenceCycleRate(sequence, poseParameter) * playbackRate;
        float advance = flInterval * flCycleRate;

        float flNewCycle = cycle + advance;
        if (flNewCycle < 0.0 || flNewCycle > 1.0f){
            flNewCycle -= (int) flNewCycle;
            /*if ((sequence.flags & mstudioseqdesc_t.STUDIO_LOOPING) == mstudioseqdesc_t.STUDIO_LOOPING || true){
            }else{
                flNewCycle = Math.max(0.0f, Math.min(1.0f, flNewCycle));
                System.out.println("animation might have finished.");
            }*/

        }

        cycle = flNewCycle;
    }

    @Override
    public void update(float delta) {
        if (!paused){
            if (!sequenceFinished){
                studioFrameAdvance();
            }
        }else{
             animationTime = (float) Engine.currentTime(); //keep setting this, in order to avoid jumps when resuming from pause
        }
    }

    @Override
    public void toggle() {
        super.toggle();
        System.out.println("paused on: " + cycle);
    }

    @Override
    public void step() {
        super.step();

        float flNewCycle = (float) Math.round(cycle * 100.0f) / 100.0f;
        flNewCycle += 0.01f;

        if (flNewCycle < 0.0 || flNewCycle > 1.0f){
            flNewCycle -= (int) flNewCycle;
        }

        cycle = flNewCycle;
    }

    static int ipoCalls = 0;
    private void initPose(Vector3f[] pos, Quaternionf[] q, int boneMask){
        //System.out.println("call to initPose: " + (++ipoCalls));
        var file = sequence.file;

        for (int i = 0; i < file.header.numbones; i++){
            var pbone = file.bones.get(i);
            q[i].set(pbone.quat);
            pos[i].set(pbone.pos);
        }
    }

    private void calcAutoPlaySequences(Vector3fX[] pos, QuaternionfX[] q, float flRealTime){
        var file = sequence.file;
        int autoplayCount = 1;

        for (int i = 0; i < autoplayCount; i++){
            int sequenceIndex = 0;
            var seq = file.sequences.get(sequenceIndex);

            if ((seq.flags & mstudioseqdesc_t.STUDIO_AUTOPLAY) == mstudioseqdesc_t.STUDIO_AUTOPLAY){
                cycle = flRealTime * getSequenceCycleRate(sequence, poseParameter);
                cycle -= (int) cycle;
            }
        }
    }

    public void standardBlendingRules(Vector3fX[] pos, QuaternionfX[] q, int boneMask){
        initPose(pos, q, boneMask);
        //accumulatePose(pos, q, sequence, cycle, 1.0f, (float) Engine.currentTime());
        //accumulate layers?

        //calcautoplaysequences
        float currTime = (float) Engine.currentTime();

        calcPoseSingle(sequence, cycle, poseParameter, boneMask, currTime, pos, q);
        addSequenceLayers(pos, q, sequence, cycle, 1.f, currTime); //i don't care about... kinverse invematiscsfsfsksdl
    }

    public void buildTransformations(Vector3fX[] pos, QuaternionfX[] q, int boneMask){
        //TODO flex transformations

        var file = sequence.file;
        if (file.header2 != null){
            //System.out.println(file.modelName + " has " + file.header2.m_nBoneFlexDriverCount + " flex drivers.");
        }

        for (int i = 0; i < file.bones.size(); i++){
            if (pos[i].equals(0, 0, 0)){
                pos[i].set(((StudioModel) owner.modelHandle.get()).bones.get(i).pos);
            }
            if (q[i].equals(0, 0, 0, 0)){
                q[i].set(((StudioModel) owner.modelHandle.get()).bones.get(i).quat);
            }


            //SourceMath.quaternionNormalize(q[i]);

            owner.boneCache.getBoneInstance(i).localPosition.set(pos[i]);
            owner.boneCache.getBoneInstance(i).localQuaternion.set(q[i]);

            computeBone(i, boneMask);
        }

        owner.boneCache.toString();
    }
    public void computeBone(int index, int boneMask){
        var boneCache = owner.boneCache;
        var file = sequence.file;

        var boneDef = file.bones.get(index);

        Matrix4f boneMat = new Matrix4f();
        var meInst = boneCache.getBoneInstance(index);
        boolean process = true;

        //do we have a bone merge component
        if (owner.getEntity().hasComponent(BoneMergeComponent.class)){
            var myName = boneDef.boneName;

            BoneMergeComponent component = owner.getEntity().getComponent(BoneMergeComponent.class);
            var prInst = component.boneMergeReference.boneCache.getBoneInstance(myName);

            if (prInst != null){
                meInst.worldScale.set(prInst.worldScale);
                meInst.worldPosition.set(prInst.worldPosition);
                meInst.worldQuaternion.set(prInst.worldQuaternion);

                process = false;
            }
        }

        if (process){
            Vector3fX posAccum = new Vector3fX(), pWorldPos = new Vector3fX(), pWorldScale = new Vector3fX();
            QuaternionfX pWorldQ = new QuaternionfX(0,0,0,1);

            if (boneDef.parent == -1){
                pWorldPos.set(0);
                pWorldScale.set(1);
            }else{
                var prInst = boneCache.getBoneInstance(boneDef.parent);
                boneCache.getBoneWorldPosition(prInst.boneDef.index, pWorldPos);
                boneCache.getBoneWorldScale(prInst.boneDef.index, pWorldScale);
                boneCache.getBoneWorldQuaternion(prInst.boneDef.index, pWorldQ);
            }

            pWorldScale.mul(meInst.localScale, meInst.worldScale); //multiply parent instance world scale and my local scale, store in world scale
            meInst.localPosition.mul(pWorldScale, posAccum); //multiply position by parent world scale
            SourceMath.vectorRotate(posAccum, pWorldQ, meInst.worldPosition); //rotate world position by parent world quaternion, store into 'ug' - temp vec3
            meInst.worldPosition.add(pWorldPos); //add my parent's position and my position together
            SourceMath.quaternionMult(pWorldQ, meInst.localQuaternion, meInst.worldQuaternion); //multiply parent instance world quaternion and my local quaternion, store in world quaternion


            //in order:
            //multiply my parent's world scale (which is retrieved into pWorldScale vec3) by this.localScale (which I assume is 1, 1, 1)
            //multiply my position by pWorldScale into posAccum
            //rotate posAcum by pWorldQ (parent world quaternion) and store in this.worldPos
            //add this.worldpos and parent.getworldposition [pWorldPos], store in this.worldPos
            //multiply parent world quaternion and my local quaternion and store in worldQuat

            meInst.toString();
        }

        SourceMath.quaternionMatrix(meInst.worldQuaternion, meInst.worldPosition, boneMat, 1.0f); //create a matrix out of our information
        SourceMath.concatTransforms(boneMat, boneDef.poseToBone, meInst.worldTransform); //lastly, multiply by inverse bind

        //meInst.worldTransform.toString();
        //meInst.worldTransform.identity();
        //System.out.println(meInst.worldTransform);
    }

    public void setupBones(int boneMask){
        Vector3fX[] pos = Stream.generate(Vector3fX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(Vector3fX[]::new);
        QuaternionfX[] q = Stream.generate(QuaternionfX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(QuaternionfX[]::new);

        standardBlendingRules(pos, q, boneMask); //calculating everything.
        buildTransformations(pos, q, boneMask); //boneComputed has to do with IK, I'm not worrying about IK right now.
    }


    public void setSequence(mstudioseqdesc_t newSeq){
        this.sequence = newSeq;

        poseParameter = new float[sequence.file.poseParameters.size()];
        for (int j = 0; j < sequence.file.poseParameters.size(); j++){
            poseParameter[j] = 0.0f;
            //System.out.println("pose parameter: " + sequence.file.poseParameters.get(j).name);
        }

        animationTime = (float) Engine.currentTime();
    }
    public void reset(){
        this.cycle = 0.0f;
        this.playbackRate = 1.0f;
    }

    public float getSequenceCycleRate(mstudioseqdesc_t sequence, float[] poseParam){
        //System.out.println("getting sequencce cycle rate: " + sequence.sequenceName);

        mstudioanimdesc_t[] pAnim = new mstudioanimdesc_t[4];
        float[] weight = new float[4];

        sequence.file.studio_seqAnims(sequence, poseParam, pAnim, weight);

        float t = 0;
        for (int i = 0; i < 4; i++){
            if (weight[i] > 0 && pAnim[i].numframes > 1){
                t += (pAnim[i].fps / (pAnim[i].numframes - 1)) * weight[i];
            }
        }

        //System.out.println("t: " + t);
        return t;
    }

    public int sequenceMaxFrame(mstudioseqdesc_t sequence, float[] poseParam){
        //System.out.println("getting sequence max frame: " + sequence.sequenceName);

        mstudioanimdesc_t[] pAnim = new mstudioanimdesc_t[4];
        float[] weight = new float[4];

        sequence.file.studio_seqAnims(sequence, poseParam, pAnim, weight);

        float maxFrame = 0;

        for (int i = 0; i < 4; i++){
            if (weight[i] > 0){
                maxFrame += pAnim[i].numframes * weight[i];
            }
        }

        if (maxFrame > 1){
            maxFrame -= 1;
        }

        return (int) (maxFrame + 0.01f);
    }

    private boolean poseIsAllZeros(mstudioseqdesc_t sequence, int i0, int i1){
        int animIndex = sequence.file.iRelativeAnim(sequence, sequence.anim(i0, i1));
        mstudioanimdesc_t anim = sequence.file.animations.get(animIndex);
        boolean result = ((anim.flags & mstudioseqdesc_t.STUDIO_ALLZEROS) == mstudioseqdesc_t.STUDIO_ALLZEROS);
        if (result){
             //System.out.println("sequence: " + sequence.sequenceName + " relative anim " + i0 + ", " + i1 + " (" + anim.animName + ") is all-zero. (animindex " + animIndex + ")");
        }
        return result;
    }

    public void calcAnimation(Vector3fX[] pos, QuaternionfX[] q,
                              mstudioseqdesc_t sequence, mstudioanimdesc_t animation, float cycle, int boneMask){

        int iFrame;
        float s;

        float fFrame = cycle * (animation.numframes - 1);
        iFrame = (int) fFrame;
        s = (fFrame - iFrame);

        int[] iLocalFrame = {iFrame};
        mstudioanim_t panim = sequence.file.pAnim(animation, iLocalFrame);

        //System.out.println("Calculating for frame: " + iFrame + " of anim " + animation.animName);

        int pWeight = 0;
        float pWeightValue;

        var file = sequence.file;
        var bones = sequence.file.bones;

        //System.out.println("*** begin frame " + iFrame + " ***");

        //bone_setup.cpp @ 1099
        for (int i = 0; i < bones.size(); i++, pWeight++){
            pWeightValue = sequence.boneWeights.get(pWeight);

            int boneFlagsToUse = file.boneFlags(i);

            if (panim != null && panim.bone == i){
                if (pWeightValue > 0){
                    mstudiobone_t bone = bones.get(i);
                    if (cycle > 0){
                        if (animation.sectionframes != 0){
                            iLocalFrame[0] %= animation.sectionframes;
                        }
                    }
                    //System.out.println("\tbone " + i);

                    calcBoneQuaternion(file, iLocalFrame[0], s, bone.quat, bone.rot, bone.rotscale, bone.qAlignment, boneFlagsToUse, panim, q[i]);
                    calcBonePosition(file, iLocalFrame[0], s, bone.pos, bone.posscale, panim, pos[i]);
                }
                panim = panim.next(sequence.file.reader);
            }else if (pWeightValue > 0){

                if ((animation.flags & mstudioseqdesc_t.STUDIO_DELTA) == mstudioseqdesc_t.STUDIO_DELTA){
                    q[i].set(0.0f, 0.0f, 0.0f, 1.0f);
                    pos[i].set(0.0f, 0.0f, 0.0f);
                }else{
                    mstudiobone_t bone = bones.get(i);

                    pos[i].set(bone.pos);
                    q[i].set(bone.quat);
                }
            }
        }

        //System.out.println("*** end frame " + iFrame + " ***");
    }

    static int cbqcalls = 0;
    //bone_setup.cpp: 374
    private void calcBoneQuaternion(StudioModel file, int iLocalFrame, float s, QuaternionfX quat, RadianEuler rot, Vector3fX rotScale, QuaternionfX qAlignment, int flags, mstudioanim_t panim, QuaternionfX qOut){
        //System.out.println("calls " + (++cbqcalls));
        ++cbqcalls;

        if ((panim.flags & mstudioanim_t.STUDIO_ANIM_RAWROT) == mstudioanim_t.STUDIO_ANIM_RAWROT){
            //System.out.println("\t\tquat type: quat p48");
            qOut.set(panim.pQuat48(file.reader).toQuaternion());
            return;
        }
        if ((panim.flags & mstudioanim_t.STUDIO_ANIM_RAWROT2) == mstudioanim_t.STUDIO_ANIM_RAWROT2){
            QuaternionfX pq64 = panim.pQuat64(file.reader).toQuaternion();
            qOut.set(pq64);
            //noted that we swap some values around... let's try that (TODO investigate if this is worth it)
            qOut.set(pq64.z, pq64.y, pq64.x, pq64.w);
            //System.out.println("\t\tquat type: p64");

            return;
        }

        if ((panim.flags & mstudioanim_t.STUDIO_ANIM_ANIMROT) == mstudioanim_t.STUDIO_ANIM_ANIMROT){
            mstudioanim_valueptr_t pValuesPtr = panim.pRotV(file.reader);
            //.out.println("\t\tquat type: animrot");

            if (s > 0.001f){
                QuaternionfX q1 = new QuaternionfX(), q2 = new QuaternionfX();
                Vector3f angle1 = new Vector3f(), angle2 = new Vector3f();

                //loadout.tf seems to indicate we only care about reading one value?
                for (int i = 0; i < 3; i++){
                    if (pValuesPtr.offset[i] != 0){
                        int offset = pValuesPtr.pAnimValue(i);
                        angle1.setComponent(i, extractAnimValue(file, iLocalFrame, offset, rotScale.component(i)));
                    }
                }
                angle2.set(angle1);


                if ((panim.flags & mstudioanim_t.STUDIO_ANIM_DELTA) != mstudioanim_t.STUDIO_ANIM_DELTA){
                    angle1.x = angle1.x + rot.x;
                    angle1.y = angle1.y + rot.y;
                    angle1.z = angle1.z + rot.z;

                    angle2.x = angle2.x + rot.x;
                    angle2.y = angle2.y + rot.y;
                    angle2.z = angle2.z + rot.z;
                }
                if (angle1.x() != angle2.x() || angle1.y() != angle2.y() || angle1.z() != angle2.z()){
                    SourceMath.angleQuaternion(angle1, q1);
                    SourceMath.angleQuaternion(angle2, q2);
                    SourceMath.quaternionBlend(q1, q2, s, qOut);
                }else{
                    SourceMath.angleQuaternion(angle1, qOut);
                }
            }else{
                Vector3f angle = new Vector3f();
                for (int i = 0; i < 3; i++){
                    if (pValuesPtr.offset[i] != 0){
                        int offset = pValuesPtr.pAnimValue(i);
                        angle.setComponent(i, extractAnimValue(file, iLocalFrame, offset, rotScale.component(i)));
                    }
                }

                if ((panim.flags & mstudioanim_t.STUDIO_ANIM_DELTA) != mstudioanim_t.STUDIO_ANIM_DELTA){
                    angle.x = angle.x + rot.x;
                    angle.y = angle.y + rot.y;
                    angle.z = angle.z + rot.z;
                }

                SourceMath.angleQuaternion(angle, qOut);
            }
        }else if (((panim.flags) & mstudioanim_t.STUDIO_ANIM_DELTA) == mstudioanim_t.STUDIO_ANIM_DELTA){
            //System.out.println("\t\tquat type: delta");
            qOut.set(0.0f, 0.0f, 0.0f, 1.0f);
        }else{
            //System.out.println("\t\tquat type: anim base");
            qOut.set(quat);
        }

        if (((panim.flags & mstudioanim_t.STUDIO_ANIM_DELTA) != mstudioanim_t.STUDIO_ANIM_DELTA) && ((flags & mstudiobone_t.BONE_FIXED_ALIGNMENT) == mstudiobone_t.BONE_FIXED_ALIGNMENT)){
            SourceMath.quaternionAlign(qAlignment, qOut, qOut);
        }
    }

    private float extractAnimValue(StudioModel file, int frame, int pAnimOffset, float scale){
        if (pAnimOffset == 0){
            return 0;
        }

        var in = file.reader;
        in.position(pAnimOffset);

        int k = frame;
        int l = 0;
        int pAnimValueNumValid = 0;
        int pAnimValueNumTotal = 0;

        do {
            l++;
            if (l > 1){
                int newPos = in.position() + (mstudioanimvalue_t.SIZE * pAnimValueNumValid);
                in.position(newPos);
            }

            k -= pAnimValueNumTotal;
            pAnimValueNumValid = in.get();
            pAnimValueNumTotal = in.get();

        } while (pAnimValueNumTotal <= k && l < 30);

        //initially, you used panimvalue.structoffset
        //you replaced it with initialPAnimValuePos. this is to try and see whether panimvalue[k + 1] is an absolute reference into the array, or if it's been affected by the adding to the array earlier in the while loop.

        //tests performed 31/03/24 confirm that in the following scenario:
        //float array[] = {0,1,2,3};
        //float* ptrToArray = array;
        //*ptrToArray = array[0] currently.
        //*ptrToArray++ again means it now points to array[1]
        //*ptrToArray++ again means it now points to array[2]
        //*ptrToArray[0] now means array[2];

        //that means that in the original source, panimvalue[k + 1] is relative to current panimvalue, and we're getting a mstudioanimvalue_t at panimvalue + (mstudioanimvalue_t.SIZE * (k + 1)).
        if (k >= pAnimValueNumValid){
            k = pAnimValueNumValid - 1;
        }

        int newPos = in.position() + (mstudioanimvalue_t.SIZE * k);
        in.position(newPos);

        short inVal = in.getShort();
        float returnValue = inVal * scale;

        return returnValue;
    }

    static int cbpcalls = 0;
    private void calcBonePosition(StudioModel file, int frame, float s, Vector3fX bonePos, Vector3fX bonePosScale, mstudioanim_t panim, Vector3fX pos){
        ++cbpcalls;

        if ((panim.flags & mstudioanim_t.STUDIO_ANIM_RAWPOS) == mstudioanim_t.STUDIO_ANIM_RAWPOS){
            //System.out.println("\t\tpos type: anim rawpos");
            pos.set(panim.pPos(file.reader).toVector());
            return;
        }else if ((panim.flags & mstudioanim_t.STUDIO_ANIM_ANIMPOS) == mstudioanim_t.STUDIO_ANIM_ANIMPOS){
            mstudioanim_valueptr_t pPosV = panim.pPosV(file.reader);
            //System.out.println("\t\tpos type: anim animpos");

            if (s > 0.001f){
                for (int j = 0; j < 3; j++){
                    if (pPosV.offset[j] != 0){
                        float v = extractAnimValue(file, frame, pPosV.pAnimValue(j), bonePosScale.component(j));
                        float jValue = v * (1.0f - s) + v * s;
                        pos.setComponent(j, jValue);
                    }
                }
            }else{
                for (int j = 0; j < 3; j++){
                    if (pPosV.offset[j] != 0){
                        float jValue = extractAnimValue(file, frame, pPosV.pAnimValue(j), bonePosScale.component(j));
                        pos.setComponent(j, jValue);
                    }
                }
            }

            if ((panim.flags & mstudioanim_t.STUDIO_ANIM_DELTA) != mstudioanim_t.STUDIO_ANIM_DELTA){
                float newX = pos.x + bonePos.x;
                float newY = pos.y + bonePos.y;
                float newZ = pos.z + bonePos.z;
                pos.set(newX, newY, newZ);
            }
        }else if ((panim.flags & mstudioanim_t.STUDIO_ANIM_DELTA) == mstudioanim_t.STUDIO_ANIM_DELTA){
            //System.out.println("\t\tpos type: anim delta");
            pos.set(0, 0, 0);
        }else{
            //System.out.println("\t\tpos type: anim base");
            pos.set(bonePos);
        }
    }

    private void accumulatePose(Vector3f[] pos, QuaternionfX[] q, mstudioseqdesc_t sequence, float cycle, float flWeight, float flTime){
        Vector3fX[] pos2 = Stream.generate(Vector3fX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(Vector3fX[]::new);
        QuaternionfX[] q2 = Stream.generate(QuaternionfX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(QuaternionfX[]::new);

        //flWeight = (float) Math.max(0.0, Math.min(1.0, flWeight));

        int boneMask = mstudiobone_t.BONE_USED_BY_ANYTHING;

        if ((sequence.flags & mstudioseqdesc_t.STUDIO_LOCAL) == mstudioseqdesc_t.STUDIO_LOCAL){
            initPose(pos2, q2, boneMask);
            //sometin funky
        }
        if (calcPoseSingle(sequence, cycle, poseParameter, boneMask, flTime, pos2, q2)){
            addLocalLayers(pos2, q2, sequence, cycle, 1.0f, flTime);
            slerpBones(sequence, pos, q, pos2, q2, flWeight, boneMask);
        }

        //addsequencelayers
        addSequenceLayers(pos, q, sequence, cycle, flWeight, flTime); //i don't care about... kinverse invematiscsfsfsksdl
    }

    private void slerpBones(mstudioseqdesc_t sequence, Vector3f[] pos1, QuaternionfX[] q1, Vector3f[] pos2, QuaternionfX[] q2, float s, int boneMask){
        if (s <= 0.0f){
            return;
        }
        if (s > 1.0f){
            s = 1.0f;
        }

        if ((sequence.flags & mstudioseqdesc_t.STUDIO_WORLD) == mstudioseqdesc_t.STUDIO_WORLD){
            worldSpaceSlerp(pos1, q1, sequence, pos2, q2, s, boneMask);
            return;
        }

        var file = sequence.file;

        float[] pS2 = new float[file.header.numbones];
        for (int i = 0; i < file.header.numbones; i++){
            if (pos1[i].equals(0,0,0)){ //temp hack
                pos1[i].set(pos2[i]);
            }
            if (q1[i].equals(0, 0, 0, 1)){
                q1[i].set(q2[i]);
            }
            pS2[i] = s * sequence.boneWeights.get(i);

            /*

            if ((file.boneFlags(i) & boneMask) == 0){
                pS2[i] = 0.0f;
            }else{
            }*/
        }

        float s1, s2;
        if ((sequence.flags & mstudioseqdesc_t.STUDIO_DELTA) == mstudioseqdesc_t.STUDIO_DELTA){
            for (int i = 0; i < file.header.numbones; i++){
                s2 = pS2[i];
                if (s2 <= 0.0f){
                    continue;
                }

                if ((sequence.flags & mstudioseqdesc_t.STUDIO_POST) == mstudioseqdesc_t.STUDIO_POST){
                    SourceMath.quaternionMA(q1[i], s2, q2[i], q1[i]);
                    var p1V = pos1[i];
                    var p2V = pos2[i];
                    p1V.set(p1V.x + p2V.x * s2,
                            p1V.y + p2V.y * s2,
                            p1V.z + p2V.z * s2);
                }else{
                    SourceMath.quaternionSM(s2, q2[i], q1[i], q1[i]);
                    var p1V = pos1[i];
                    var p2V = pos2[i];
                    p1V.set(p1V.x + p2V.x * s2,
                            p1V.y + p2V.y * s2,
                            p1V.z + p2V.z * s2);
                }
            }
            return;
        }

        QuaternionfX q3 = new QuaternionfX();
        for (int i = 0; i < file.header.numbones; i++){
            s2 = pS2[i];
            if (s2 <= 0.0f){
                continue;
            }
            s1 = 1.0f - s2;

            if ((file.boneFlags(i) & mstudiobone_t.BONE_FIXED_ALIGNMENT) == mstudiobone_t.BONE_FIXED_ALIGNMENT){
                SourceMath.quaternionSlerpNoAlign(q2[i], q1[i], s1, q3);
            }else{
                SourceMath.quaternionSlerp(q2[i], q1[i], s1, q3);
            }

            q1[i].set(q3);
            var p1V = pos1[i];
            var p2V = pos2[i];
            p1V.set(p1V.x * s1 + p2V.x * s2,
                    p1V.y * s1 + p2V.y * s2,
                    p1V.z * s1 + p2V.z * s2);
        }

    }

    private void worldSpaceSlerp(Vector3f[] pos1, QuaternionfX[] q1, mstudioseqdesc_t sequence, Vector3f[] pos2, QuaternionfX[] q2, float s, int boneMask){
        float s1; //weight of parent for q2, pos2;
        float s2; //weight for q2, pos2

        System.out.println("WORLD SPACE SLERP CALLED - CURRENTLY A NO OP");

    }

    private static int alocCalls = 0;
    private void addLocalLayers(Vector3f[] pos, QuaternionfX[] q, mstudioseqdesc_t sequence, float cycle, float flWeight, float flTime){
        if ((sequence.flags & STUDIO_LOCAL) != STUDIO_LOCAL){
            return;
        }
        for (int i = 0; i < sequence.numautolayers; i++){
            var pLayer = sequence.autoLayers.get(i);
            if (!((pLayer.flags & STUDIO_AL_LOCAL) == STUDIO_AL_LOCAL)){
                continue;
            }

            float layerCycle = cycle;
            float layerWeight = flWeight;

            if (pLayer.start != pLayer.end){
                float s = 1.0f;
                if (cycle < pLayer.start){
                    continue;
                }
                if (cycle >= pLayer.end){
                    continue;
                }
                if (cycle < pLayer.peak && pLayer.start != pLayer.peak){
                    s = (cycle - pLayer.start) / (pLayer.peak - pLayer.start);
                }else if (cycle > pLayer.tail && pLayer.end != pLayer.tail){
                    s = (pLayer.end - cycle) / (pLayer.end - pLayer.tail);
                }

                if ((pLayer.flags & STUDIO_AL_SPLINE) == STUDIO_AL_SPLINE){
                    s = SourceMath.simpleSpline(s);
                }

                if (((pLayer.flags & STUDIO_AL_XFADE) == STUDIO_AL_XFADE) && (cycle > pLayer.tail)){
                    layerWeight = (s * flWeight) / (1 - flWeight + s * flWeight);
                }else if ((pLayer.flags & STUDIO_AL_NOBLEND) == STUDIO_AL_NOBLEND){
                    layerWeight = s;
                }else{
                    layerWeight = flWeight * s;
                }

                layerCycle = (cycle - pLayer.start) / (pLayer.end - pLayer.start);
            }

            int iSequence = sequence.file.iRelativeSequence(sequence, pLayer.iSequence);
            accumulatePose(pos, q, sequence.file.getSequence(iSequence), layerCycle, layerWeight, flTime);
        }
    }

    private boolean calcPoseSingle(mstudioseqdesc_t sequence, float cycle, float[] poseParameter, int boneMask, float flTime, Vector3fX[] pos, QuaternionfX[] q){
        int[] i0 = {0}, i1 = {0};
        float[] s0 = {0}, s1 = {0};

        boolean bResult = true;

        Vector3fX[] pos2 = Stream.generate(Vector3fX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(Vector3fX[]::new);
        QuaternionfX[] q2 = Stream.generate(QuaternionfX::new).limit(StudioModel.MAX_STUDIO_BONES).toArray(QuaternionfX[]::new);

        var file = sequence.file;

        sequence.file.localPoseParameter(sequence, poseParameter, 0, s0, i0);
        sequence.file.localPoseParameter(sequence, poseParameter, 1, s1, i1);

        if ((sequence.flags & mstudioseqdesc_t.STUDIO_REALTIME) == mstudioseqdesc_t.STUDIO_REALTIME){
            cycle = flTime * getSequenceCycleRate(sequence, poseParameter);
            cycle -= (int) cycle;
        }else if ((sequence.flags & mstudioseqdesc_t.STUDIO_CYCLEPOSE) == mstudioseqdesc_t.STUDIO_CYCLEPOSE){
            int iPose = sequence.file.getSharedPoseParameter(sequence, sequence.cycleposeindex); //getSharedPoseParameter?
            if (iPose != -1){ //
                cycle = poseParameter[iPose];
            }else{
                cycle = 0.0f;
            }
        }else if (cycle < 0 || cycle >= 1){
            if ((sequence.flags & mstudioseqdesc_t.STUDIO_LOOPING) == mstudioseqdesc_t.STUDIO_LOOPING){
                cycle = cycle - (int) cycle;
                if (cycle < 0){
                    cycle += 1;
                }
            }else{
                cycle = Math.max(0.0f, Math.min(1.0f, cycle));
            }
        }

        if (s0[0] < 0.001f){
            if (s1[0] < 0.001f){
                if (poseIsAllZeros(sequence, i0[0], i1[0])){
                    bResult = false;
                }else{
                    calcAnimation(pos, q, sequence, sequence.file.animations.get(sequence.anim(i0[0], i1[0])), cycle, boneMask);
                }
            }else if (s1[0] > 0.999f){
                calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0], i1[0] + 1)), cycle, boneMask);
            }else{
                calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0], i1[0])), cycle, boneMask);
                calcAnimation(pos2, q2, sequence, file.animations.get(sequence.anim(i0[0], i1[0] + 1)), cycle, boneMask);
                blendBones(q, pos, sequence, q2, pos2, s1[0], boneMask);
            }
        }else if (s0[0] > 0.999){
            if (s1[0] < 0.001){
                if (poseIsAllZeros(sequence, i0[0] + 1, i1[0])){
                    bResult = false;
                }else{
                    calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0])), cycle, boneMask);
                }
            }else if (s1[0] > 0.999){
                calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0] + 1)), cycle, boneMask);
            }else{
                calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0])), cycle, boneMask);
                calcAnimation(pos2, q2, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0] + 1)), cycle, boneMask);
                blendBones(q, pos, sequence, q2, pos2, s1[0], boneMask);
            }
        }else{
            if (s1[0] < 0.001){
                if (poseIsAllZeros(sequence, i0[0] + 1, i1[0])){
                    calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0], i1[0])), cycle, boneMask);
                    scaleBones(q, pos, sequence, 1.0f - s0[0], boneMask);
                }else if (poseIsAllZeros(sequence, i0[0], i1[0])){
                    calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0])), cycle, boneMask);
                    scaleBones(q, pos, sequence, s0[0], boneMask);
                }else{
                    calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0], i1[0])), cycle, boneMask);
                    calcAnimation(pos2, q2, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0])), cycle, boneMask);

                    blendBones(q, pos, sequence, q2, pos2, s0[0], boneMask);
                }
            }else if (s1[0] > 0.999){
                calcAnimation(pos, q, sequence, file.animations.get(sequence.anim(i0[0], i1[0] + 1)), cycle, boneMask);
                calcAnimation(pos2, q2, sequence, file.animations.get(sequence.anim(i0[0] + 1, i1[0] + 1)), cycle, boneMask);
                blendBones(q, pos, sequence, q2, pos2, s0[0], boneMask);
            }else{
                int[] iAnimIndices = new int[3];
                float[] weight = new float[3];

                calc3WayBlendIndices(i0[0], i1[0], s0[0], s1[0], sequence, iAnimIndices, weight);

                if (weight[1] < 0.001){
                    calcAnimation(pos, q, sequence, file.animations.get(iAnimIndices[0]), cycle, boneMask);
                    calcAnimation(pos2, q2, sequence, file.animations.get(iAnimIndices[2]), cycle, boneMask);
                    blendBones(q, pos, sequence, q2, pos2, weight[2] / (weight[0] + weight[2]), boneMask);
                }else{
                    calcAnimation(pos, q, sequence, file.animations.get(iAnimIndices[0]), cycle, boneMask);
                    calcAnimation(pos2, q2, sequence, file.animations.get(iAnimIndices[1]), cycle, boneMask);

                    blendBones(q, pos, sequence, q2, pos2, weight[1] / (weight[0] + weight[1]), boneMask);

                    calcAnimation(pos2, q2, sequence, file.animations.get(iAnimIndices[2]), cycle, boneMask);
                    blendBones(q, pos, sequence, q2, pos2, weight[2], boneMask);
                }
            }
        }

        return bResult;
    }

    private void scaleBones(QuaternionfX[] q1, Vector3f[] pos1, mstudioseqdesc_t sequence, float s, int boneMask){
        var file = sequence.file;

        int j;

        float s2 = s;
        float s1 = 1.0f - s2;
        for (int i = 0; i < file.header.numbones; i++){
            if ((file.boneFlags(i) & boneMask) == 0){
                continue;
            }
            j = i;

            if (sequence.boneWeights.get(j) > 0.0){
                SourceMath.quaternionIdentityBlend(q1[i], s1, q1[i]);
                SourceMath.vectorScale(pos1[i], s2, pos1[i]);
            }
        }
    }

    private void calc3WayBlendIndices(int i0, int i1, float s0, float s1, mstudioseqdesc_t sequence, int[] pAnimIndices, float[] pWeight){
        boolean bEven = (((i0 + i1) & 1) == 0);

        int x1, y1;
        int x2, y2;
        int x3, y3;

        //TL TO BR
        if (bEven){
            if (s0 > s1){
                //B
                x1 = 0;
                y1 = 0;
                x2 = 1;
                y2 = 0;
                x3 = 1;
                y3 = 1;
                pWeight[0] = (1.0f - s0);
                pWeight[1] = (s0 - s1);
            }else{
                //C
                x1 = 1;
                y1 = 1;
                x2 = 0;
                y2 = 1;
                x3 = 0;
                y3 = 0;
                pWeight[0] = s0;
                pWeight[1] = s1 - s0;
            }
        }else{
            float flTotal = s0 + s1;
            if (flTotal > 1.0f){
                //D
                x1 = 1;
                y1 = 0;
                x2 = 1;
                y2 = 1;
                x3 = 0;
                y3 = 1;
                pWeight[0] = (1.0f - s1);
                pWeight[1] = s0 - 1.0f + s1;
            }else{
                //A
                x1 = 0;
                y1 = 1;
                x2 = 0;
                y2 = 0;
                x3 = 1;
                y3 = 0;
                pWeight[0] = s1;
                pWeight[1] = 1.0f - s0 - s1;
            }
        }

        pAnimIndices[0] = sequence.anim(i0 + x1, i1 + y1);
        pAnimIndices[1] = sequence.anim(i0 + x2, i1 + y2);
        pAnimIndices[2] = sequence.anim(i0 + x3, i1 + y3);

        if (pWeight[1] < 0.001f){
            pWeight[1] = 0.0f;
        }
        pWeight[2] = 1.0f - pWeight[0] - pWeight[1];
    }

    private void blendBones(QuaternionfX[] q, Vector3f[] pos, mstudioseqdesc_t sequence, QuaternionfX[] q2, Vector3f[] pos2, float s, int boneMask){
        QuaternionfX q3 = new QuaternionfX();
        var file = sequence.file;

        int j;
        if (s <= 0){
            return;
        }else if (s >= 1.0f){
            for (int i = 0; i < file.bones.size(); i++){
                if ((file.boneFlags(i) & boneMask) == 0){
                    continue;
                }
                //skipping something about a seqGroup. is vmodel ever something i need to care about?
                j = i;
                if (sequence.boneWeights.get(j) > 0.0){
                    q[i].set(q2[i]);
                    pos[i].set(pos2[i]);
                }
            }
            return;
        }

        float s2 = s;
        float s1 = 1.0f - s2;

        for (int i = 0; i < file.header.numbones; i++){
            int bflags = file.boneFlags(i);
            if ((bflags & boneMask) == 0){
                continue;
            }

            j = i;

            if (sequence.boneWeights.get(j) > 0.0f){
                if ((bflags & mstudiobone_t.BONE_FIXED_ALIGNMENT) == mstudiobone_t.BONE_FIXED_ALIGNMENT){
                    SourceMath.quaternionBlendNoAlign(q2[i], q[i], s1, q3);
                }else{
                    SourceMath.quaternionBlend(q2[i], q[i], s1, q3);
                }

                q[i].set(q3);

                pos[i].x = pos[i].x * s1 + pos2[i].x * s2;
                pos[i].y = pos[i].y * s1 + pos2[i].y * s2;
                pos[i].z = pos[i].z * s1 + pos2[i].z * s2;
            }
        }
    }

    static int asqlCalls = 0;

    private void addSequenceLayers(Vector3f[] pos, QuaternionfX[] q, mstudioseqdesc_t sequence, float cycle, float flWeight, float flTime){
        //System.out.println("call to addsequencelayers: " + (++asqlCalls) + " - sequence has " + sequence.numautolayers + " auto layers.");
        /*if ((sequence.flags & mstudioseqdesc_t.STUDIO_LOCAL) == mstudioseqdesc_t.STUDIO_LOCAL){
            System.out.println("sequence is a 'local' sequence");
        }*/
        var file = sequence.file;

        for (int i = 0; i < sequence.numautolayers; i++){
            mstudioautolayer_t pLayer = sequence.autoLayers.get(i);
            if ((pLayer.flags & STUDIO_AL_LOCAL) == STUDIO_AL_LOCAL){
                continue; //dunno why we skip these - there is no information in the original code
            }

            float layerCycle = cycle;
            float layerWeight = flWeight;

            if (pLayer.start != pLayer.end){
                float s = 1.0f;
                float index = 0;

                if ((pLayer.flags & mstudioautolayer_t.STUDIO_AL_POSE) != mstudioautolayer_t.STUDIO_AL_POSE){
                    index = cycle;
                }else{
                    int iSequence = file.iRelativeSequence(sequence, pLayer.iSequence);
                    int iPose = file.getSharedPoseParameter(iSequence, pLayer.iPose);
                    if (iPose != -1){
                        mstudioposeparamdesc_t Pose = file.pPoseParameter(iPose);
                        index = poseParameter[iPose] * (Pose.end - Pose.start) + Pose.start;
                    }else{
                        index = 0;
                    }
                }
                if (index < pLayer.start){
                    continue;
                }
                if (index >= pLayer.end){
                    continue;
                }
                if (index < pLayer.peak && pLayer.start != pLayer.peak){
                    s = (index - pLayer.start) / (pLayer.peak - pLayer.start);
                }else if (index > pLayer.tail && pLayer.end != pLayer.tail){
                    s = (pLayer.end - index) / (pLayer.end - pLayer.tail);
                }

                if ((pLayer.flags & STUDIO_AL_SPLINE) == STUDIO_AL_SPLINE){
                    s = SourceMath.simpleSpline(s);
                }

                if ((pLayer.flags & STUDIO_AL_XFADE) == STUDIO_AL_XFADE && (index > pLayer.tail)){
                    layerWeight = (s * flWeight) / (1 - flWeight + s * flWeight);
                }else if ((pLayer.flags & STUDIO_AL_NOBLEND) == STUDIO_AL_NOBLEND){
                    layerWeight = s;
                }else{
                    layerWeight = flWeight * s;
                }

                if (!((pLayer.flags & mstudioautolayer_t.STUDIO_AL_POSE) == mstudioautolayer_t.STUDIO_AL_POSE)){
                    layerCycle = (cycle - pLayer.start) / (pLayer.end - pLayer.start);
                }
            }

            //int iSequence = iRelativeSequence(sequence, pLayer.iSequence);
            accumulatePose(pos, q, file.sequences.get(pLayer.iSequence), layerCycle, layerWeight, flTime);
        }
    }
}
