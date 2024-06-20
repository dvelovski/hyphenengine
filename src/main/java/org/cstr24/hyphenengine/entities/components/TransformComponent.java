package org.cstr24.hyphenengine.entities.components;

import org.joml.Matrix4f;
import org.joml.Vector3fX;

public class TransformComponent extends HyComponent {
    public static final String TYPE = composeTypeName(BASE_NAMESPACE, "transform");

    public Vector3fX position;
    public Vector3fX rotation;
    public Vector3fX scale;

    public TransformComponent(){
        position = new Vector3fX();
        rotation = new Vector3fX();
        scale = new Vector3fX(1);
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

    public Matrix4f computeWorldTransform(){
        return new Matrix4f();
    }
}
