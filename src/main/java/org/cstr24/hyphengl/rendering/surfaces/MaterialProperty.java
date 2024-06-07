package org.cstr24.hyphengl.rendering.surfaces;

import org.cstr24.hyphengl.data.ComponentType;
import org.cstr24.hyphengl.rendering.shader.ShaderUniform;

public class MaterialProperty {
    public String name = "";
    public String parameterBinding = ""; //we need to know what we're setting in the shader
    public ShaderUniform shaderParameter;
    public String description = "";

    public ComponentType componentType;

    public Object initialValue;
    public Object currentValue;

    public PropertyMetadata metadata;

    public void set(Object value){
        currentValue = value;
    }

    public MaterialProperty bindToParameter(HyMaterial mat, String bindingName){
        this.parameterBinding = bindingName;
        mat.bindMaterialProperty(this);
        return this;
    }
    public MaterialProperty doBind(ShaderUniform param){
        this.shaderParameter = param;
        return this;
    }

    public PropertyMetadata getMetadata(){
        return metadata;
    }

    /*public void setTexturePropertyMetadata(MaterialProperty _prop, int _txType){
        setTexturePropertyMetadata(_prop, _txType, TextureTypeFilter.None);
    }
    public void setTexturePropertyMetadata(MaterialProperty _prop, int _txType, TextureTypeFilter _filter){
        var txMetadata = new TextureMetadata();
        txMetadata.textureType = _txType;
        txMetadata.filter = _filter;

        _prop.metadata = txMetadata;
    }*/

    public Object getCurrentValue(){
        return this.currentValue;
    }
}
