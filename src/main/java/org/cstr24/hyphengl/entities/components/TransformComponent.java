package org.cstr24.hyphengl.entities.components;

import org.joml.Vector3fX;

public class TransformComponent extends Component<TransformComponent> {
    public static final String TYPE = "hyphen.transform";

    public Vector3fX position;
    public Vector3fX rotation;
    public Vector3fX scale;

    public TransformComponent(){
        position = new Vector3fX();
        rotation = new Vector3fX();
        scale = new Vector3fX();
    }

    @Override
    public String getComponentType() {
        return TYPE;
    }

    @Override
    public String getComponentSimpleName() {
        return "Transform";
    }

    @Override
    public TransformComponent reset() {
        position.set(0);
        rotation.set(0);
        scale.set(1);
        return this;
    }

    @Override
    public TransformComponent cloneComponent() {
        TransformComponent component = new TransformComponent();
        component.position.set(this.position);
        component.rotation.set(this.rotation);
        component.scale.set(this.scale);
        return component;
    }

    @Override
    public TransformComponent create() {
        return new TransformComponent();
    }

    @Override
    public void update(float delta) {

    }
}
