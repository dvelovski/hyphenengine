package org.cstr24.hyphengl.entities.components;

import org.cstr24.hyphengl.animation.AnimationInstance;
import org.cstr24.hyphengl.assets.HyResHandle;
import org.cstr24.hyphengl.geometry.BoneCache;
import org.cstr24.hyphengl.geometry.HyModel;
import org.cstr24.hyphengl.geometry.SubMeshState;
import org.joml.Matrix4f;

import java.util.ArrayList;

public class ModelComponent extends Component<ModelComponent>{
    public static final String TYPE = "hyphen.modelinstance";

    public HyResHandle<HyModel> modelHandle;
    public ArrayList<SubMeshState> subMeshStates;

    public ArrayList<AnimationInstance> animationInstances;
    public BoneCache boneCache;

    public ModelComponent(){
        subMeshStates = new ArrayList<>();
        animationInstances = new ArrayList<>();
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

    @Override
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

    public void setModelHandle(HyResHandle<HyModel> mHandle){
        this.modelHandle = mHandle;
        //create submesh states
    }

    public Matrix4f[] getSkeleton(){
        Matrix4f[] boneMats = new Matrix4f[modelHandle.get().hyBones.size()]; //number of bonz
        return boneCache.getBoneWorldMatrices(boneMats);
    }

    public void setModelReference(HyResHandle<HyModel> modelRef){
        this.modelHandle = modelRef;
        this.boneCache = new BoneCache();
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
