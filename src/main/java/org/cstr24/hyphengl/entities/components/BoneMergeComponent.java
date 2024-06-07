package org.cstr24.hyphengl.entities.components;

public class BoneMergeComponent extends Component<BoneMergeComponent>{
    public static final String TYPE = "hyphen.sourceinterop.bonemergecomponent";

    public ModelComponent boneMergeReference;
    public int boneMergeBoneIndex;

    @Override
    public String getComponentType() {
        return TYPE;
    }

    @Override
    public String getComponentSimpleName() {
        return "Bone Merge";
    }

    @Override
    public BoneMergeComponent reset() {
        return this;
    }

    @Override
    public BoneMergeComponent cloneComponent() {
        return new BoneMergeComponent();
    }

    @Override
    public BoneMergeComponent create() {
        return new BoneMergeComponent();
    }

    @Override
    public void update(float delta) {

    }
}
