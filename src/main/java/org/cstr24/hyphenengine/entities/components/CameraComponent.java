package org.cstr24.hyphenengine.entities.components;

import org.cstr24.hyphenengine.scene.ComponentSystem;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraComponent extends HyComponent{
    public static final String TYPE = composeTypeName(HyComponent.BASE_NAMESPACE, "camera");

    public float fov = 90f;

    public float yaw = -90f;
    public float pitch = 0f;

    public float zNear = 0.01f;
    public float zFar = 1000f;

    public Vector3f cameraFront;
    public Vector3f cameraUp;
    public Vector3f cameraRight;
    public Vector3f worldUp;

    public Matrix4f projectionMatrix;
    public Matrix4f viewMatrix;

    public float aspectRatio;

    public CameraComponent(){
        cameraRight = new Vector3f();
        cameraUp = new Vector3f(0, 0, 0);

        worldUp = new Vector3f(0, 1, 0);
        cameraFront = new Vector3f(0, 0, -1);

        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }

    @Override
    public String getComponentType() {
        return TYPE;
    }

    @Override
    public String getComponentSimpleName() {
        return "Camera";
    }

    @Override
    public CameraComponent reset() {
        return this;
    }

    @Override
    public CameraComponent cloneComponent() {
        return new CameraComponent();
    }

    @Override
    public CameraComponent create() {
        return new CameraComponent();
    }
}
