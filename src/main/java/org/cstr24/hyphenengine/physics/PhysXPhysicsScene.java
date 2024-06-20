package org.cstr24.hyphenengine.physics;

import physx.physics.PxScene;

public class PhysXPhysicsScene extends PhysicsScene{
    public PxScene scene;



    @Override
    public void destroy() {

        scene.release();
    }
}
