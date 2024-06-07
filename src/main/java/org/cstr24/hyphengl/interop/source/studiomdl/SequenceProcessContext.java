package org.cstr24.hyphengl.interop.source.studiomdl;

import org.cstr24.hyphengl.interop.source.studiomdl.structs.mstudioanim_t;
import org.cstr24.hyphengl.interop.source.studiomdl.structs.mstudiobone_t;

import java.util.HashMap;

public class SequenceProcessContext {
    public float m_flPlaybackRate;
    public float m_flAnimTime;
    public float m_Cycle;
    public float[] m_flPoseParameter;
    public HashMap<Integer, mstudioanim_t> animBlockCache;
    public int boneMask = mstudiobone_t.BONE_USED_BY_ANYTHING;

    public SequenceProcessContext(){
        m_flPoseParameter = new float[StudioModel.MAX_STUDIO_POSE_PARAM];
        animBlockCache = new HashMap<>();
    }
}
