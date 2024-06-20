package org.cstr24.hyphenengine.entities;

import dev.dominion.ecs.api.Entity;
import org.cstr24.hyphenengine.entities.components.HyComponent;
import org.cstr24.hyphenengine.entities.components.TransformComponent;
import org.cstr24.hyphenengine.scene.Scene;

import java.util.ArrayList;

public abstract class HyEntity {
    private String entityName = "";
    private int entityID;

    private Scene hostWorld;

    private HyEntity parentEntity;
    private Entity ecsEntity;
    //entity follow mode - maintain distance?

    private final ArrayList<HyEntity> children;
    private final ArrayList<HyComponent> components;

    private final TransformComponent transform;

    private boolean spawned;

    public HyEntity(){
        components = new ArrayList<>();
        children = new ArrayList<>();

        transform = new TransformComponent();
    }
    public HyEntity(Scene host){
        this();
        this.hostWorld = host;
    }

    public Scene getHostWorld(){
        return hostWorld;
    }
    public void setHostWorld(Scene host){
        this.hostWorld = host;
    }
    public HyEntity getParentEntity(){
        return parentEntity;
    }
    public void setParentEntity(HyEntity parent){
        this.parentEntity = parent;
    }

    public ArrayList<HyEntity> getChildren(){
        return children;
    }

    public ArrayList<HyComponent> getComponents(){
        return components;
    }
    public <T extends HyComponent> T getComponent(Class<T> type){
        return ecsEntity.get(type);
    }
    public boolean hasComponent(Class<?> type){
        return ecsEntity.has(type);
    }

    public int getEntityID(){
        return entityID;
    }
    public void setEntityID(int id){
        this.entityID = id;
    }

    public abstract <T extends HyEntity> T cloneEntity();

    public boolean isSpawned(){
        return spawned;
    }
    public void setSpawned(boolean status){
        this.spawned = status;
    }

    public void setECSEntity(Entity ent){
        this.ecsEntity = ent;
    }
    public Entity getECSEntity(){
        return this.ecsEntity;
    }

    public abstract void update();

    public void setEntityName(String name){
        this.entityName = name;
    }

    public String getEntityName() {
        return entityName;
    }
}
