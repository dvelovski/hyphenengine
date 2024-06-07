package org.cstr24.hyphengl.rendering.surfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class MaterialManager {
    private static Map<String, HyMaterial> materialLibrary;

    private static MaterialManager theManager;
    public static MaterialManager get(){
        return (theManager == null ? (theManager = new MaterialManager()) : theManager);
    }

    public MaterialManager(){
        materialLibrary = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }
    public void addMaterial(HyMaterial _toAdd){
        System.out.println("added: " + _toAdd.materialName);
        materialLibrary.put(_toAdd.materialName, _toAdd);
    }
    public HyMaterial getMaterial(String key){
        return materialLibrary.get(key);
    }
}
