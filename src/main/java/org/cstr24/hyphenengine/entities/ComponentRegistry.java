package org.cstr24.hyphenengine.entities;

import dev.dominion.ecs.api.Composition;
import org.cstr24.hyphenengine.entities.components.HyComponent;

import java.util.HashMap;

public class ComponentRegistry {
    //Engine owns the component registry.

    private final HashMap<String, HyComponent> registeredComponentTypes;
    private final HashMap<String, Composition>  compositionRegistry;

    public ComponentRegistry(){
        registeredComponentTypes = new HashMap<>();
        compositionRegistry = new HashMap<>();
    }

    public void registerComponentType(HyComponent component){
        registeredComponentTypes.put(component.getComponentType(), component);
    }

    public HashMap<String, HyComponent> getRegisteredComponentTypes(){
        return registeredComponentTypes;
    }

    public HyComponent createComponent(String type){
        return registeredComponentTypes.get(type).create();
    }
}
