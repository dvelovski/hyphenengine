package org.cstr24.hyphengl.rendering.surfaces;

import org.cstr24.hyphengl.data.ComponentType;
import org.cstr24.hyphengl.rendering.shader.HyShader;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Supplier;

public abstract class HyMaterial{
    public int materialID;

    public String materialName = "";

    public String shaderName = "";
    private HyShader shaderReference;

    public LinkedHashMap<String, MaterialProperty> materialProperties = new LinkedHashMap<>(); //this is so we can iterate them in order and dump values into a buffer
    //public HashMap<String, TextureMetadata> textureMetadataMap = new HashMap<>();
    //public HashMap<String, String> uniformReferences = new HashMap<>();
    /* ^ commenting out until I find a reason to need these */

    public Supplier<? extends  MaterialInstance> materialInstanceSupplier = (Supplier<MaterialInstance>) MaterialInstance::new;

    public MaterialProperty createProperty(String _binding, ComponentType _type, Object _initValue){
        var matProperty = new MaterialProperty();
        matProperty.parameterBinding = _binding;
        matProperty.name = _binding;
        matProperty.componentType = _type;
        matProperty.initialValue = matProperty.currentValue = _initValue;

        addMaterialProperty(matProperty);

        return matProperty;
    }

    public MaterialTextureProperty createTextureProperty(String _binding, int _idx){
        var texProperty = new MaterialTextureProperty();
        texProperty.parameterBinding = _binding;
        texProperty.name = _binding;
        texProperty.slot = _idx;
        addMaterialProperty(texProperty);

        return texProperty;
    }

    public void addMaterialProperty(MaterialProperty _prop){
        materialProperties.put(_prop.name, _prop);
        bindMaterialProperty(_prop);
    }

    public boolean bindMaterialProperty(MaterialProperty _prop){
        if (shaderReference != null){
            return bindMatPropertyUnsafe(_prop);
        }
        return false;
    }
    private boolean bindMatPropertyUnsafe(MaterialProperty _prop){
        if (shaderReference.hasUniform(_prop.parameterBinding)){
            _prop.doBind(shaderReference.getUniform(_prop.parameterBinding));
            return true;
        }
        return false;
    }

    public MaterialInstance createInstance(){
        var newInst = materialInstanceSupplier.get();
        newInst.materialReference = this;
        return newInst;
    }

    public boolean containsProperty(String propertyName){
        return materialProperties.containsKey(propertyName);
    }
    public Object getPropertyValue(String propertyName){
        var result = materialProperties.get(propertyName);
        return result.currentValue == null ? result.initialValue : result.currentValue;
    }
    public HyShader getShader(){
        return shaderReference;
    }

    public HyMaterial setShader(HyShader _shader) {
        shaderName = _shader.getShaderName();
        shaderReference = _shader;

        materialProperties.values().forEach(this::bindMatPropertyUnsafe);

        return this;
    }
    public abstract void shaderUpdated();

    public ArrayList<MaterialTextureProperty> getTextureProperties(){
        var list = new ArrayList<MaterialTextureProperty>();
        materialProperties.values().stream().filter(inst -> (inst instanceof MaterialTextureProperty))
                .forEach(inst -> list.add((MaterialTextureProperty) inst));
        return list;
    }

    public int compareInstances(MaterialInstance i1, MaterialInstance i2){
        return Integer.compare(i1.materialReference.materialID, i2.materialReference.materialID);
    }
}
