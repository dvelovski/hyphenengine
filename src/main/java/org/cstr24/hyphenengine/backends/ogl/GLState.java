package org.cstr24.hyphenengine.backends.ogl;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL41;

/**
 * Stores internal OpenGL state for the non-DSA backend.
 */
public class GLState {

    private static int boundTextureTarget;
    private static int boundTextureName;

    private static int activeTextureSlot;

    private static int boundVertexArray;

    private static int boundVertexBufferTarget;
    private static int boundVertexBuffer;

    public static void bindTexture(int target, int texName){
        if (target != boundTextureTarget || boundTextureName != texName){
            GL20.glBindTexture(target, texName);

            boundTextureTarget = target;
            boundTextureName = texName;
        }
    }
    public static void activeTexture(int slot){
        if (slot != activeTextureSlot){
            GL20.glActiveTexture(slot);
            activeTextureSlot = slot;
        }
    }
    public static void bindVertexArray(int vao){
        if (vao != boundVertexArray){
            GL41.glBindVertexArray(vao);
            boundVertexArray = vao;
        }
    }
    public static void bindBuffer(int target, int vbo){
        if (target != boundVertexBufferTarget || vbo != boundVertexBuffer){
            GL41.glBindBuffer(target, vbo);

            boundVertexBufferTarget = target;
            boundVertexBuffer = vbo;
        }
    }
}
