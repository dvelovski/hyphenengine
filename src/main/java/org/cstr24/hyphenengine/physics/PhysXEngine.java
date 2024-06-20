package org.cstr24.hyphenengine.physics;

import physx.PxTopLevelFunctions;
import physx.common.*;
import physx.physics.PxPhysics;

public class PhysXEngine extends APhysicsEngine {
    public int versionMajor;
    public int versionMinor;
    public int versionMicro;

    PxDefaultAllocator allocator;
    PxDefaultErrorCallback errorCb;
    PxFoundation foundation;

    PxTolerancesScale tolerances;
    PxPhysics physics;

    PxDefaultCpuDispatcher dispatcher;

    public PhysXEngine(){
        int version = PxTopLevelFunctions.getPHYSICS_VERSION();
        versionMajor = version >> 24;
        versionMinor = (version >> 16) & 0xff;
        versionMicro = (version >> 8) & 0xff;
        System.out.println("PhysX version: " + versionMajor + ", " + versionMinor + ", " + versionMicro);

        PxDefaultAllocator allocator = new PxDefaultAllocator();
        PxDefaultErrorCallback errorCb = new PxDefaultErrorCallback();
        PxFoundation foundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb);

        PxTolerancesScale tolerances = new PxTolerancesScale();
        PxPhysics physics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances);

        int numThreads = 4;
        PxDefaultCpuDispatcher cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads);

    }

    public PhysXPhysicsScene createScene(){
        return new PhysXPhysicsScene();
    }
}
