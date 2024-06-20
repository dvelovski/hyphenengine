package org.cstr24.hyphenengine.rendering.surfaces;

import java.util.HashMap;

public class MaterialInstance implements Comparable<MaterialInstance>{
    public HyMaterial materialReference;

    public HashMap<String, Object> propertyOverrides = new HashMap<>();

    public MaterialInstance(){

    }
    public MaterialInstance(HyMaterial _ref){
        this.materialReference = _ref;
    }

    public void setProperty(String propertyName, Object value){
        if (materialReference.containsProperty(propertyName)){
            propertyOverrides.put(propertyName, value);
        }
    }

    public void resetProperty(String propertyName){
        propertyOverrides.remove(propertyName);
    }

    public Object getPropertyValue(String propertyName){
        if (materialReference.containsProperty(propertyName)){
            return propertyOverrides.getOrDefault(
                propertyName,
                materialReference.getPropertyValue(propertyName)
            );
        }
        return null;
    }

    public MaterialInstance clone(){
        var clone = new MaterialInstance();
        clone.materialReference = this.materialReference;
        clone.propertyOverrides.putAll(propertyOverrides);
        return clone;
    }

    @Override
    public int compareTo(MaterialInstance o) {
        return materialReference.compareInstances(this, o);
    }

    //parameter buffer:
    //traverse material's properties and dump their initial values to the buffer
    //if we have overrides, dump it instead

    //naive bind:
    //bind the material's referenced shader
    //traverse material's properties and set uniforms to current except if we have overrides
    //if we have overrides, set those instead


}
