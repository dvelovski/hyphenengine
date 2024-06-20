package org.cstr24.hyphenengine.geometry;

import org.joml.*;

public class Bone {
    public Bone parent;
    public Vector3fX basePosition;
    public QuaternionfX baseRotation;


    private Matrix4f bindPose;

    public String name = "";
    public int index;


    public Bone(){
        bindPose = new Matrix4f();
    }

    public Bone setBasePosition(Vector3fX bPos){
        this.basePosition = bPos;
        return this;
    }
    public Bone setBaseRotation(QuaternionfX bRot){
        this.baseRotation = bRot;
        return this;
    }

    public void calculateInverseBindPose(){
        Matrix4f rot = new Matrix4f();
        rot.rotate(baseRotation);
        Matrix4f trans = new Matrix4f();
        trans.translate(basePosition);

        trans.mul(rot, bindPose);
        bindPose.invert();

        System.out.println("matrix trans x rot");
        System.out.println(bindPose);

        System.out.println("matrix translationrotate function");
        System.out.println(new Matrix4f().translationRotate(basePosition, baseRotation).invert());
    }
    public Matrix4f getInverseBindPose(){
        return bindPose;
    }
}
