package org.cstr24.hyphenengine.rendering.shader;

import org.cstr24.hyphenengine.data.ElementDescriptor;
import org.joml.*;

import java.util.List;

public abstract class ShaderUniform {
    public String name = "";
    public int program;
    public int location;

    public ElementDescriptor format; //i.e. is it a float? a mat4? this will tell us. an array perhaps?

    public ShaderUniform(int _prog, int _loc, String _name, ElementDescriptor _fmt){
        this.program = _prog;
        this.location = _loc;
        this.name = _name;
        this.format = _fmt;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " {" +
                "name='" + name + '\'' +
                ", program=" + program +
                ", location=" + location +
                ", format=" + format +
                '}';
    }

    public abstract void setFloat(float x);
    public abstract void setFloatVec2(float x, float y);
    public abstract void setFloatVec2(Vector2f v2);
    public abstract void setFloatVec3(float x, float y, float z);
    public abstract void setFloatVec3(Vector3f v3);
    public abstract void setFloatVec4(float x, float y, float z, float w);
    public abstract void setFloatVec4(Vector4f v4);

    public abstract void setInt(int x);
    public abstract void setIntVec2(int x, int y);
    public abstract void setIntVec2(Vector2i v2);
    public abstract void setIntVec3(int x, int y, int z);
    public abstract void setIntVec3(Vector3i v3);
    public abstract void setIntVec4(int x, int y, int z, int w);
    public abstract void setIntVec4(Vector4i v4);

    public abstract void setMat3(Matrix3f m3);
    public abstract void setMat4(Matrix4f m4);

    public abstract void setMat4ArrayFromList(List<Matrix4f> m4s);
    public abstract void setMat4Array(Matrix4f[] m4s);
}
