package org.cstr24.hyphengl.rendering.shader;

import java.util.HashMap;

public class ShaderManager {
    private static HashMap<String, HyShader> shaderLibrary;
    private int currentShader = -1;

    private static ShaderManager theManager;
    public static ShaderManager get(){
        return (theManager == null ? (theManager = new ShaderManager()) : theManager);
    }

    ShaderManager(){
        shaderLibrary = new HashMap<>();
    }

    public void addShader(HyShader _toAdd){
        shaderLibrary.put(_toAdd.shaderName, _toAdd);
    }
    public HyShader getShader(String key){
        return shaderLibrary.get(key);
    }

    public boolean shaderBound(int id){
        return this.currentShader == id;
    }
    public int getCurrentShader(){
        return this.currentShader;
    }
    public void setCurrentShader(int id){
        this.currentShader = id;
        //System.out.println("new shader bound: " + id);
    }
}
