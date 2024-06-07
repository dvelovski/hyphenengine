package org.cstr24.hyphengl.rendering.camera;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class HyCameraTest {
    public Vector3f position;
    public Vector3f target;
    public Vector3f direction;
    public Vector3f up;
    public Vector3f worldUp;
    public Vector3f cameraRight;
    public Vector3f cameraUp;
    public Vector3f cameraFront;

    public HyCameraTest(){
        position = new Vector3f(0f, 0f, 3f);
        target = new Vector3f(0f, 0f, 0f);
        direction = position.sub(target, new Vector3f()).normalize();

        up = new Vector3f(0f, 1f, 0f);
        worldUp = new Vector3f(up);

        cameraRight = up.cross(direction, new Vector3f()).normalize();
        cameraUp = direction.cross(cameraRight, new Vector3f()).normalize();

        cameraFront = new Vector3f(0f, 0f, -1f);
    }

    public void updateVectors(){
        cameraFront.cross(worldUp, cameraRight).normalize();
        cameraRight.cross(cameraFront, up).normalize();
    }
    public Matrix4f getViewMatrix(){
        //System.out.println("getting matrix: " + position.add(cameraFront, new Vector3f()));
        return new Matrix4f().lookAt(position, position.add(cameraFront, new Vector3f()), cameraUp);
    }
}
