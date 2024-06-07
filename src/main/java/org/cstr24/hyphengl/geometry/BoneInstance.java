package org.cstr24.hyphengl.geometry;

import org.joml.Matrix4f;
import org.joml.QuaternionfX;
import org.joml.Vector3fX;

public class BoneInstance {
    public Matrix4f worldTransform;
    public Bone boneDef;
    public QuaternionfX localQuaternion;
    public QuaternionfX worldQuaternion;
    public Vector3fX localScale;
    public Vector3fX worldScale;
    public Vector3fX localPosition;
    public Vector3fX worldPosition;

    private BoneCache owner;

    public BoneInstance() {
        worldTransform = new Matrix4f();

        localQuaternion = new QuaternionfX();
        worldQuaternion = new QuaternionfX();
        localScale = new Vector3fX(1, 1, 1);
        worldScale = new Vector3fX();
        localPosition = new Vector3fX();
        worldPosition = new Vector3fX();
    }
    public BoneInstance setBoneDef(Bone bone){
        this.boneDef = bone;
        return this;
    }
    public BoneInstance setOwner(BoneCache newOwner){
        this.owner = newOwner;
        return this;
    }
    public void reset(){
        worldTransform.identity();

        localQuaternion.set(0, 0, 0, 1);
        worldQuaternion.set(0, 0, 0, 1);
        localScale.set(1);
        worldScale.set(1);
        localPosition.zero();
        worldPosition.zero();
    }
}
