package org.cstr24.hyphengl.entities;

import org.cstr24.hyphengl.entities.components.Component;
import org.cstr24.hyphengl.entities.components.TransformComponent;
import org.cstr24.hyphengl.scene.World;

import java.util.ArrayList;

public abstract class Entity<T extends Entity<?>> {
    private String entityName = "";
    private int entityID;

    private World hostWorld;
    private Entity<?> parentEntity;
    private final ArrayList<Entity<?>> children;
    private final ArrayList<Component<?>> components;

    private final TransformComponent transform;

    private boolean spawned;

    public Entity(){
        components = new ArrayList<>();
        children = new ArrayList<>();

        transform = new TransformComponent();
    }
    public Entity(World host){
        this();
        this.hostWorld = host;
    }

    public World getHostWorld(){
        return hostWorld;
    }
    public Entity<?> getParentEntity(){
        return parentEntity;
    }
    public void setParentEntity(Entity<?> parent){
        this.parentEntity = parent;
    }

    public ArrayList<Entity<?>> getChildren(){
        return children;
    }

    public ArrayList<Component<?>> getComponents(){
        return components;
    }
    public void addComponent(Component<?> component){
        components.add(component);
    }
    public Component<?> getComponent(String type){
        return components.stream().filter(comp -> comp.getComponentType().equals(type)).findFirst().orElse(null);
    }
    public <C extends Component<?>> C getComponent(Class<C> compClass){
        return (C) components.stream().filter(comp -> comp.getClass() == compClass).findFirst().orElse(null);
    }
    public Component<?>[] getComponentsOfType(String type){
        return components.stream().filter(comp -> comp.getComponentType().equals(type)).toArray(Component[]::new);
    }
    public boolean hasComponent(String type){
        return components.stream().anyMatch(comp -> comp.getComponentType().equals(type));
    }

    public int getEntityID(){
        return entityID;
    }
    public void setEntityID(int id){
        this.entityID = id;
    }

    public abstract T cloneEntity();

    public boolean isSpawned(){
        return spawned;
    }
    public void setSpawned(boolean status){
        this.spawned = status;
    }

    public abstract void update();
}
