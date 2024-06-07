package org.cstr24.hyphengl.rendering.shader;

import org.cstr24.hyphengl.data.ArrayElementDescriptor;
import org.cstr24.hyphengl.data.ElementDescriptor;
import org.joml.*;
import org.lwjgl.opengl.GL45;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.lang.Math;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class GLShaderUniform extends ShaderUniform {
    public GLShaderUniform(int _prog, int _loc, String _name, ElementDescriptor _fmt) {
        super(_prog, _loc, _name, _fmt);
    }

    @Override
    public void setFloat(float x) {
        GL45.glProgramUniform1f(this.program, this.location, x);
    }

    @Override
    public void setFloatVec2(float x, float y) {
        GL45.glProgramUniform2f(this.program, this.location, x, y);
    }

    @Override
    public void setFloatVec2(Vector2f v2) {
        GL45.glProgramUniform2f(this.program, this.location, v2.x, v2.y);
    }

    @Override
    public void setFloatVec3(float x, float y, float z) {
        GL45.glProgramUniform3f(this.program, this.location, x, y, z);
    }

    @Override
    public void setFloatVec3(Vector3f v3) {
        GL45.glProgramUniform3f(this.program, this.location, v3.x, v3.y, v3.z);
    }

    @Override
    public void setFloatVec4(float x, float y, float z, float w) {
        GL45.glProgramUniform4f(this.program, this.location, x, y, z, w);
    }

    @Override
    public void setFloatVec4(Vector4f v4) {
        GL45.glProgramUniform4f(this.program, this.location, v4.x, v4.y, v4.z, v4.w);
    }

    @Override
    public void setInt(int x) {
        GL45.glProgramUniform1i(this.program, this.location, x);
    }

    @Override
    public void setIntVec2(int x, int y) {
        GL45.glProgramUniform2i(this.program, this.location, x, y);
    }

    @Override
    public void setIntVec2(Vector2i v2) {
        GL45.glProgramUniform2i(this.program, this.location, v2.x, v2.y);
    }

    @Override
    public void setIntVec3(int x, int y, int z) {
        GL45.glProgramUniform3i(this.program, this.location, x, y, z);
    }

    @Override
    public void setIntVec3(Vector3i v3) {
        GL45.glProgramUniform3i(this.program, this.location, v3.x, v3.y, v3.z);
    }

    @Override
    public void setIntVec4(int x, int y, int z, int w) {
        GL45.glProgramUniform4i(this.program, this.location, x, y, z, w);
    }

    @Override
    public void setIntVec4(Vector4i v4) {
        GL45.glProgramUniform4i(this.program, this.location, v4.x, v4.y, v4.z, v4.w);
    }

    @Override
    public void setMat3(Matrix3f m3) {
        try (MemoryStack stack = MemoryStack.stackPush()){
            var fBuff = stack.mallocFloat(9); //9 floats, not bytes, just BTW!
            m3.get(fBuff);

            GL45.glProgramUniformMatrix3fv(program, location, false, fBuff);
        }
    }

    @Override
    public void setMat4(Matrix4f m4) {
        try (MemoryStack stack = MemoryStack.stackPush()){
            var fBuff = stack.mallocFloat(16); //16 floats, not bytes, just BTW!
            m4.get(fBuff);

            GL45.glProgramUniformMatrix4fv(program, location, false, fBuff);
        }
    }

    @Override
    public void setMat4ArrayFromList(List<Matrix4f> m4s) {
        if (this.format.isArray()){
            var descriptor = ((ArrayElementDescriptor) this.format);
            if (m4s.size() > descriptor.length){
                //going to update anyway, but only up to descriptor.length
                System.out.println("GLShaderUniform: Too many mat4s provided (" + m4s.size() + " given, room for " + descriptor.length + ")");
            }
            int elementCount = Math.min(m4s.size(), descriptor.length); //if we have too few m4s we don't want to run out of bounds

            try (MemoryStack stack = MemoryStack.stackPush()){
                var fBuff = stack.mallocFloat(16 * elementCount);

                //System.out.println("GLShaderUniform: setMat4Array setting " + elementCount + " array elements.");
                for (int i = 0; i < elementCount; i++){
                    m4s.get(i).get(i * 16, fBuff);
                }

                fBuff.rewind();

                GL45.glProgramUniformMatrix4fv(program, location, false, fBuff);

            }
        }
    }

    @Override
    public void setMat4Array(Matrix4f[] m4s) {
        if (this.format.isArray()){
            var descriptor = ((ArrayElementDescriptor) this.format);
            if (m4s.length > descriptor.length){
                //going to update anyway, but only up to descriptor.length
                System.out.println("GLShaderUniform: Too many mat4s provided (" + m4s.length + " given, room for " + descriptor.length + ")");
            }
            int elementCount = Math.min(m4s.length, descriptor.length); //if we have too few m4s we don't want to run out of bounds

            try (MemoryStack stack = MemoryStack.stackPush()){
                var fBuff = stack.mallocFloat(16 * elementCount);

                //System.out.println("GLShaderUniform: setMat4Array setting " + elementCount + " array elements.");
                for (int i = 0; i < elementCount; i++){
                    m4s[i].get(i * 16, fBuff);
                }

                fBuff.rewind();

                GL45.glProgramUniformMatrix4fv(program, location, false, fBuff);

                fBuff.rewind();
                /*float[] testx = new float[fBuff.limit()];
                fBuff.get(testx);
                System.out.println(testx);*/
            }
        }
    }

    public void setMat4ArrayInternal(Stream<Matrix4f> in, int nElements){

    }

}
