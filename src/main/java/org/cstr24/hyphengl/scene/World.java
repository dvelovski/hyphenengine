package org.cstr24.hyphengl.scene;

import org.cstr24.hyphengl.entities.ComponentRegistry;
import org.cstr24.hyphengl.entities.components.Component;
import org.cstr24.hyphengl.entities.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class World {
    private static final Logger LOGGER = Logger.getLogger(World.class.getName());

    private int entityHandleCounter;
    private HashMap<Integer, Entity<?>> idToEntityMap;
    private HashMap<String, ArrayList<Component<?>>> componentStore;
    private ComponentRegistry registryHandle;

    public World(){
        componentStore = new HashMap<>();
    }
    public void initializeComponentStore(ComponentRegistry registry){
        registryHandle = registry;

        registry.getRegisteredComponentTypes().keySet().forEach(key -> {
            componentStore.put(key, new ArrayList<>());
        });
    }

    public Entity<?> cloneEntity(Entity<?> source){
        return cloneEntity(source, false);
    }
    public Entity<?> cloneEntity(Entity<?> source, boolean cloneID){
        final Entity<?> resultingEntity = source.cloneEntity();

        source.getComponents().forEach(component -> {
            if (componentRegistered(component.getComponentType())){
                Component<?> cloneResult = component.cloneComponent();

                resultingEntity.addComponent(cloneResult);
                resultingEntity.setParentEntity(resultingEntity);
                cloneResult.setOwner(resultingEntity);
            }else{
                LOGGER.log(Level.WARNING, "Could not clone component of type " + component.getComponentType() + " as it is not registered.");
            }
        });

        if (cloneID){
            resultingEntity.setEntityID(source.getEntityID());
        }
        return resultingEntity;
    }
    public void spawnEntity(Entity<?> toSpawn){
        if (!toSpawn.isSpawned()){
            if (toSpawn.getEntityID() != -1){
                idToEntityMap.put(toSpawn.getEntityID(), toSpawn);
            }else{
                int entID = entityHandleCounter++;
                idToEntityMap.put(entID, toSpawn);
                toSpawn.setEntityID(entID);
            }

            //need to register all its components
            toSpawn.getComponents().forEach(component -> {
                if (componentRegistered(component.getComponentType())){
                    componentStore.get(component.getComponentType()).add(component);
                }else{
                    LOGGER.log(Level.WARNING, "Entity " + toSpawn.getEntityID() + " has a component that is not registered: " + component.getComponentType());
                }
            });

            toSpawn.setSpawned(true);
        }
    }

    public void destroyEntity(Entity<?> target){
        destroyEntity(target, true);
    }
    public void destroyEntity(Entity<?> target, boolean destroyHierarchy){
        target.getComponents().forEach(this::destroyComponent);

        if (destroyHierarchy){
            //this assumes destroyEntity with single parameter (Entity<?> target) always passes 'true' to double-parameter version. beware of this in case you ever change it.
            target.getChildren().forEach(this::destroyEntity);
        }

        idToEntityMap.remove(target.getEntityID());
    }
    public void destroyComponent(Component<?> component){
        componentStore.get(component.getComponentSimpleName()).remove(component);
    }
    public Component<?> createComponent(String typeIdentifier){
        if (componentRegistered(typeIdentifier)){
            return registryHandle.createComponent(typeIdentifier);
        }else{
            LOGGER.log(Level.SEVERE, "Cannot create component of type " + typeIdentifier + " as it is not registered.");
            return null;
        }
    }

    public void createComponentAndAddToEntity(Entity<?> recipient, String componentType){
        Component<?> newComponent = createComponent(componentType);
        if (newComponent != null){
            recipient.addComponent(newComponent);
        }
    }

    public boolean componentRegistered(String typeIdentifier){
        return registryHandle.getRegisteredComponentTypes().containsKey(typeIdentifier);
    }

    public Stream<Entity<?>> getAllEntities(){
        return idToEntityMap.values().stream();
    }

    /**
     * Removes the entity from the scene but does not make any attempts to destroy it.
     * @param id The entity ID to remove.
     */
    public void removeEntity(int id){
        idToEntityMap.remove(id);
    }
    public void removeEntity(Entity<?> entity){
        removeEntity(entity.getEntityID());
    }
}
