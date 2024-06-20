package org.cstr24.hyphenengine.rendering;

import imgui.extension.imguizmo.flag.Mode;
import org.cstr24.hyphenengine.assets.HyAssetHandle;
import org.cstr24.hyphenengine.entities.HyEntity;
import org.cstr24.hyphenengine.entities.components.ModelComponent;
import org.cstr24.hyphenengine.entities.components.StudioModelComponent;
import org.cstr24.hyphenengine.entities.components.TransformComponent;
import org.cstr24.hyphenengine.geometry.HyMesh;
import org.cstr24.hyphenengine.geometry.SubMesh;
import org.cstr24.hyphenengine.rendering.shader.HyShader;
import org.cstr24.hyphenengine.rendering.state.ShaderUniformState;
import org.cstr24.hyphenengine.rendering.surfaces.HyMaterial;
import org.cstr24.hyphenengine.rendering.surfaces.MaterialInstance;
import org.cstr24.hyphenengine.textures.HyTexture;
import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL20C.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS;

public class HyGLBasicRenderer extends Renderer {
    private HyMaterial boundMaterial;
    private HyShader boundShader;
    private ShaderUniformState uniformState;

    private boolean shaderDrawParameters = false;

    private PointerBuffer multiDrawIndexBuffer;

    int[] countArray = new int[8192];
    int[] baseArray = new int[8192];


    public Matrix4f model;
    public Matrix4f view;
    public Matrix4f projection;
    public Matrix4f[] boneMatrices;

    {
        initialize();
    }

    private void initialize() {
        uniformState = new ShaderUniformState();
        //query the GL
        uniformState.setTextureUnitCount(GL11.glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));
        shaderDrawParameters = GL.getCapabilities().GL_ARB_shader_draw_parameters;

        //multiDrawIndexBuffer = PointerBuffer.allocateDirect(8192);
    }

    public void drawEntity(HyEntity instance){
        ModelComponent component = null;
        if (instance.hasComponent(ModelComponent.class)){
            component = instance.getComponent(ModelComponent.class);
        }else if (instance.hasComponent(StudioModelComponent.class)){
            component = instance.getComponent(StudioModelComponent.class);
        }


        if (component != null){
            boneMatrices = component.getSkeleton();

            TransformComponent tComponent = instance.getComponent(TransformComponent.class);
            //compute final transform, SOMEWHERE... how...
            Matrix4f transform = new Matrix4f();
            transform.translate(tComponent.position);
            //transform.rotateXYZ(tComponent.rotation);
            transform.scale(tComponent.scale);

            var instanceMeshes = component.modelHandle.get().meshes;
            for (int i = 0; i < instanceMeshes.size(); i++){
                var mesh = instanceMeshes.get(i);
                draw(component, mesh, transform);
            }
        }
    }

    public void draw(ModelComponent instance, HyMesh mesh, Matrix4f modelTransform) {
                mesh.bind();

        ArrayList<SubMesh> subMeshes = mesh.subMeshes;
        for (int i = 0; i < subMeshes.size(); i++) {
            SubMesh sub = subMeshes.get(i);
            if (!instance.subMeshStates.get(i).enabled) {
                continue;
            }

            bindMaterialInstance(instance.subMeshStates.get(i).materialInstance);
            drawSM(mesh, sub, modelTransform);
        }
    }

    public void drawSM(HyMesh mesh, SubMesh sub, Matrix4f modelTransform){
        boundMaterial.getShader().getUniform("model").setMat4(modelTransform);
        int meshIndexType;
        switch (mesh.indexType) {
            case UnsignedShort -> meshIndexType = GL11.GL_UNSIGNED_SHORT;
            case Short -> meshIndexType = GL11.GL_SHORT;
            default -> meshIndexType = GL11.GL_UNSIGNED_INT;
        }

        if (sub.baseVertex) {
            GL45.glDrawElementsBaseVertex(
                    GL11.GL_TRIANGLES,
                    sub.elementCount,
                    meshIndexType,
                    (long) sub.elementOffset * mesh.indexType.BYTES,
                    sub.elementBase
            );
        } else {
            GL45.glDrawElements(
                    GL11.GL_TRIANGLES,
                    sub.elementCount,
                    meshIndexType,
                    (long) sub.elementOffset * mesh.indexType.BYTES
            );
        }
    }

    private void bindMaterialInstance(MaterialInstance instance) {
        //firstly check if the material (shader really) is bound
        //TODO if the material was /not/ bound, then we need to set its initial values.
        //Then, if the instance does not contain an override, but our state is different to the material's default - need to set some uniforms

        bindMaterial(instance.materialReference);
        //System.out.println("binding material instance: " + instance.materialReference.materialName);

        instance.materialReference.getTextureProperties().forEach(txProperty -> {
            if (instance.getPropertyValue(txProperty.name) != null){
                int valueToUse = ((HyAssetHandle<HyTexture>) instance.getPropertyValue(txProperty.name)).get().getTextureID();
                if (uniformState.checkTextureUnitBinding(txProperty.slot, valueToUse)) {
                    //skip.
                    //System.out.println("Saved a switch.");
                } else {
                }
                uniformState.updateTextureUniformBinding(txProperty.slot, valueToUse);
                GL45.glBindTextureUnit(txProperty.slot, valueToUse);
            }
        });
    }

    private void bindMaterial(HyMaterial mat) {
        uniformState.reset();
        boundMaterial = mat;
        /*if (mat != boundMaterial) {

            //System.out.println("bound material: " + mat.materialName);
        }
        //System.out.println("binding material: " + mat.materialName);*/
        mat.getShader().bind();
        mat.getShader().getUniform("view").setMat4(view);
        mat.getShader().getUniform("projection").setMat4(projection);

        if (boneMatrices != null && mat.getShader().hasUniform("boneMatrices")){
            mat.getShader().getUniform("boneMatrices").setMat4Array(boneMatrices); //i hate this but it'll do for now
        }
    }

    @Override
    public void beginFrame() {
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CW);
    }

    @Override
    public void endFrame() {

    }

    @Override
    public void draw(HyEntity entity) {

    }

    @Override
    public void draw(HyMesh mesh) {
        mesh.bind();

        for (int i = 0; i < mesh.subMeshes.size(); i++){
            drawSM(mesh, mesh.subMeshes.get(i), model);
        }
    }
    //now we need to set all the uniforms to the material's defaults
    //for the per-object scope
}
