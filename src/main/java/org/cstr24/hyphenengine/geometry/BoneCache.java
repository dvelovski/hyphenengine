package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.entities.components.ModelComponent;
import org.joml.Matrix4f;
import org.joml.QuaternionfX;
import org.joml.Vector3fX;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BoneCache {
    private BoneInstance[] instances;

    private Vector3fX posAccum;
    private Vector3fX scaleAccum;
    private QuaternionfX quatAccum;
    private ModelComponent owner;

    public BoneCache(){
        posAccum = new Vector3fX();
        scaleAccum = new Vector3fX();
        quatAccum = new QuaternionfX();
    }
    public BoneCache(ModelComponent owner){
        this();
        setOwner(owner);
    }
    public void setOwner(ModelComponent owner){
        this.owner = owner;

        instances = Stream.generate(BoneInstance::new).limit(owner.modelHandle.get().getBoneCount()).toArray(BoneInstance[]::new);
        IntStream.range(0, instances.length).forEach(i -> instances[i].setBoneDef(owner.modelHandle.get().hyBones.get(i)).setOwner(this));

        //System.out.println("set owner to " + owner.modelRef.modelName + " - he has " + instances.length + " bones");
    }

    public Matrix4f[] getBoneWorldMatrices(Matrix4f[] dest){
        if (dest.length >= instances.length){
            for (int i = 0; i < instances.length; i++){
                dest[i] = instances[i].worldTransform;
            }
        }
        return dest;
    }

    public Matrix4f getBoneMatrix(int index){
        return instances[index].worldTransform;
    }

    public QuaternionfX getBoneWorldQuaternion(int index, QuaternionfX dest){
        dest = (dest == null ? new QuaternionfX() : dest);

        var bone = instances[index];
        dest.set(bone.worldQuaternion);

        return dest;
    }
    public Vector3fX getBoneWorldPosition(int index, Vector3fX dest){
        dest = (dest == null ? new Vector3fX() : dest);

        var bone = instances[index];

        dest.set(bone.worldPosition);
        return dest;
    }
    public Vector3fX getBoneWorldScale(int index, Vector3fX dest){
        dest = (dest == null ? new Vector3fX() : dest);

        var bone = instances[index];

        dest.set(bone.worldScale);
        return dest;
    }
    public BoneInstance getBoneInstance(int index){
        return index == -1 ? null : instances[index];
    }
    public BoneInstance getBoneInstance(String name){
        for (BoneInstance inst : instances){
            if (inst.boneDef.name.equals(name)){
                return inst;
            }
        }
        return null;
    }
}
