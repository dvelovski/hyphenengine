package org.cstr24.hyphengl.entities;

import org.cstr24.hyphengl.entities.components.Component;

import java.util.HashMap;

public class ComponentRegistry {
    //Engine owns the component registry.

    private final HashMap<String, Component<?>> registeredComponentTypes;

    public ComponentRegistry(){
        registeredComponentTypes = new HashMap<>();
    }

    public void registerComponentType(Component<?> component){
        registeredComponentTypes.put(component.getComponentType(), component);
    }

    public HashMap<String, Component<?>> getRegisteredComponentTypes(){
        return registeredComponentTypes;
    }

    public Component<?> createComponent(String type){
        return registeredComponentTypes.get(type).create();
    }
}
