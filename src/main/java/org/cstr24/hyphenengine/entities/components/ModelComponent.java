package org.cstr24.hyphenengine.entities.components;

import org.cstr24.hyphenengine.animation.AnimationInstance;
import org.cstr24.hyphenengine.assets.HyAssetHandle;
import org.cstr24.hyphenengine.geometry.BoneCache;
import org.cstr24.hyphenengine.geometry.HyModel;
import org.cstr24.hyphenengine.geometry.SubMeshState;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class ModelComponent extends HyComponent {
    public static final String TYPE = "hyphen.modelinstance";

    public HyAssetHandle<HyModel> modelHandle;
    public ArrayList<SubMeshState> subMeshStates;

    public ArrayList<AnimationInstance> animationInstances;
    public BoneCache boneCache;

    public ModelComponent(){
        subMeshStates = new ArrayList<>();
        animationInstances = new ArrayList<>();
        boneCache = new BoneCache();
    }

    @Override
    public String getComponentType() {
        return TYPE;
    }

    @Override
    public String getComponentSimpleName() {
        return "Model Instance";
    }

    @Override
    public ModelComponent reset() {
        modelHandle = null;
        boneCache = null;
        subMeshStates.clear();
        animationInstances.clear();

        return this;
    }

    @Override
    public ModelComponent cloneComponent() {
        ModelComponent result = new ModelComponent();
        if (modelHandle != null){
            //create new handle for the result in a clean way somehow
            //think of scenarios where cloning is a thing and whether this is actually a problem...
        }
        //result.modelHandle = this.modelHandle.get().addUser();
        return result;
    }

    @Override
    public ModelComponent create() {
        return new ModelComponent();
    }

    public void update(float delta) {
        //animation instances to update...?
        animationInstances.forEach(anim -> {
            anim.update(delta);
        });
    }

    public void addAnimationInstance(AnimationInstance instance){
        animationInstances.add(instance);
        instance.owner = this;
    }

    public Matrix4f[] getSkeleton(){
        Matrix4f[] boneMats = new Matrix4f[modelHandle.get().hyBones.size()]; //number of bonz
        return boneCache.getBoneWorldMatrices(boneMats);
    }

    public void setModel(HyAssetHandle<HyModel> modelRef){
        this.modelHandle = modelRef;
        this.boneCache.setOwner(this);
        resetSubMeshStates();
    }

    public void resetSubMeshStates(){
        subMeshStates.clear();
        modelHandle.get().meshes.getFirst().subMeshes.forEach(sMesh -> {
            SubMeshState state = new SubMeshState(sMesh, this);

            //TODO materials managed as resources like textures and meshes
            state.materialInstance = sMesh.materialInstance.clone();
            subMeshStates.add(state);
        });
    }
}
