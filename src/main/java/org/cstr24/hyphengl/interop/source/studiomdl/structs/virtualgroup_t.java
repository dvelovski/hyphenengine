package org.cstr24.hyphengl.interop.source.studiomdl.structs;

import org.cstr24.hyphengl.interop.source.studiomdl.StudioModel;

import java.util.ArrayList;

public class virtualgroup_t {
    StudioModel fileRef;

    ArrayList<Integer> boneMap; //maps global bone to local bone
    ArrayList<Integer> masterBone; //maps local bone to global bone
    ArrayList<Integer> masterSeq; //map local sequence to master sequence
    ArrayList<Integer> masterAnim; //map local animation to master animation
    ArrayList<Integer> masterAttachment; //map local attachment to global
    ArrayList<Integer> masterPose; //maps local pose parameter to global
    ArrayList<Integer> masterNode; //maps local transition nodes to global

    public virtualgroup_t(StudioModel file) {
        fileRef = file;
        boneMap = new ArrayList<>();
        masterBone = new ArrayList<>();
        masterSeq = new ArrayList<>();
        masterAnim = new ArrayList<>();
        masterAttachment = new ArrayList<>();
        masterPose = new ArrayList<>();
        masterNode = new ArrayList<>();
    }
}
