package org.cstr24.hyphenengine.geometry;

import org.cstr24.hyphenengine.assets.AResourceLoader;
import org.cstr24.hyphenengine.assets.AssetLoadTask;
import org.lwjgl.assimp.AIMesh;
import org.lwjgl.assimp.AIScene;
import org.lwjgl.assimp.Assimp;

import java.nio.ByteBuffer;

public class ModelLoader extends AResourceLoader<HyModel> {
    public HyModel fallbackModel;

    @Override
    public HyModel loadResource(String handle) {
        int flags = Assimp.aiProcess_JoinIdenticalVertices | Assimp.aiProcess_Triangulate | Assimp.aiProcess_FixInfacingNormals;
        try (AIScene aScene = Assimp.aiImportFile(handle, flags)){
            if (aScene != null){
                int meshCount = aScene.mNumMeshes();
                int skeletonCount = aScene.mNumSkeletons();
                int materialCount = aScene.mNumMaterials();
                int textureCount = aScene.mNumTextures();

                String sceneName = aScene.mName().dataString();

                System.out.println();

                for (int mesh = 0; mesh < meshCount; mesh++){
                    AIMesh aMesh = AIMesh.create(aScene.mMeshes().get(mesh));
                    System.out.println(aMesh.mName().dataString());
                }
            }

        }
        return null;
    }
    public void processMesh(AIMesh mesh){

    }

    @Override
    public void preload() {
        fallbackModel = loadResource("res/nomodel.fbx");
    }

    @Override
    public void unloadDefaults() {

    }

    @Override
    public HyModel supplyDefault() {
        return null;
    }

    @Override
    public AssetLoadTask beginAssetLoad() {
        return null;
    }

    @Override
    public void mainAssetLoad(AssetLoadTask aTask) {

    }

    @Override
    public void setAssetContents(ByteBuffer source) {

    }

    @Override
    public void setAssetContents(HyModel source) {

    }
}
