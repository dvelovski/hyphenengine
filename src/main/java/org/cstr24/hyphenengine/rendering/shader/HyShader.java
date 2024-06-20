package org.cstr24.hyphenengine.rendering.shader;

import org.cstr24.hyphenengine.core.Engine;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class HyShader {
    protected int shaderID;

    protected String shaderName = "";

    protected HashMap<String, ShaderUniform> shaderUniforms;

    public abstract HyShader shaderFromSource(ShaderType type, String source);
    public abstract HyShader shaderFromBinary(ShaderType type, ByteBuffer binary);

    public abstract HyShader link();

    public abstract HyShader introspect();

    public abstract HyShader bind();

    public abstract void printDetails();
    public int getShaderID(){
        return shaderID;
    }
    public String getShaderName(){
        return shaderName;
    }

    public void setName(String _name){
        this.shaderName = _name;
    }
    public ShaderUniform getUniform(String _uniform){
        return shaderUniforms.get(_uniform);
    }
    public boolean hasUniform(String _uniform){
        return shaderUniforms.containsKey(_uniform);
    }

    public static HyShader create(){
        return Engine.getBackend().getShaderFactory().createShader();
    }
}
