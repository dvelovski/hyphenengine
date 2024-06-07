package org.cstr24.hyphengl.geometry;

public class MeshFactory {
    private static IMeshFactory instance;

    public static void setInstance(IMeshFactory factory) {
        instance = factory;
    }

    public static HyMesh create() {
        return instance.createMesh();
    }
}
