package org.cstr24.hyphenengine.scene;

import dev.dominion.ecs.api.Dominion;
import dev.dominion.ecs.api.Entity;
import dev.dominion.ecs.api.Scheduler;
import org.cstr24.hyphenengine.entities.ComponentRegistry;
import org.cstr24.hyphenengine.entities.components.HyComponent;
import org.cstr24.hyphenengine.entities.HyEntity;
import org.cstr24.hyphenengine.entities.components.TransformComponent;
import org.cstr24.hyphenengine.entities.systems.ECSSystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Scene {
    private static final Logger LOGGER = Logger.getLogger(Scene.class.getName());

    private Dominion sceneDominion;
    private Scheduler dominionScheduler;

    private int entityHandleCounter;
    private HashMap<Integer, HyEntity> idToEntityMap;
    private HashMap<String, ArrayList<HyComponent>> componentStore;
    private ComponentRegistry registryHandle;

    private HyEntity sceneCamera;

    public Scene(){
        componentStore = new HashMap<>();
        sceneDominion = Dominion.create();
        dominionScheduler = sceneDominion.createScheduler();
        idToEntityMap = new HashMap<>();
    }

    public void destroyEntity(HyEntity target){
        destroyEntity(target, true);
    }
    public void destroyEntity(HyEntity target, boolean destroyHierarchy){
        boolean deletionResult = sceneDominion.deleteEntity(target.getECSEntity());
        if (!deletionResult){
            System.out.println("entity " + target.getEntityName() + " has already been deleted.");
        }

        /*if (destroyHierarchy){
            //this assumes destroyEntity with single parameter (HyEntity target) always passes 'true' to double-parameter version. beware of this in case you ever change it.
            target.getChildren().forEach(this::destroyEntity);
        }*/

        idToEntityMap.remove(target.getEntityID());
    }

    public Stream<HyEntity> getAllEntities(){
        return idToEntityMap.values().stream();
    }

    /**
     * Removes the entity from the scene and dominion.
     * @param id The entity ID to remove.
     */
    public void removeEntity(int id){
        HyEntity result = idToEntityMap.remove(id);
        if (result != null){
            sceneDominion.deleteEntity(result.getECSEntity());

            result.getComponents().clear();

            //remove parents
        }
    }

    public <T extends HyEntity> T createEntity(T in, HyComponent... paramComponents){
        in.setHostWorld(this);
        in.setEntityID(entityHandleCounter);
        idToEntityMap.put(entityHandleCounter++, in);

        TransformComponent tComp = new TransformComponent();

        Entity ecsEnt = sceneDominion.createEntity((Object[]) paramComponents);
        ecsEnt.add(tComp);

        in.getComponents().add(tComp);
        tComp.setOwner(in);

        for (HyComponent hyComp : paramComponents){
            in.getComponents().add(hyComp);
            hyComp.setOwner(in);
        }
        in.setECSEntity(ecsEnt);

        return in;
    }

    public HyEntity cloneEntity(HyEntity cloneSource){
        return cloneEntity(cloneSource, false, false);
    }
    public HyEntity cloneEntity(HyEntity cloneSource, boolean cloneHierarchy, boolean cloneID){
        HyEntity hyResult = cloneSource.cloneEntity();
        hyResult.setHostWorld(this);

        if (cloneID){
            idToEntityMap.put(hyResult.getEntityID(), hyResult);
        }else{
            idToEntityMap.put(entityHandleCounter++, hyResult);
        }

        //create new ECS entity
        Entity ecsEnt = sceneDominion.createEntity();

        for (HyComponent entComponent : cloneSource.getComponents()){
            HyComponent clonedComp = entComponent.cloneComponent();

            hyResult.getComponents().add(clonedComp);
            ecsEnt.add(clonedComp);

            clonedComp.setOwner(hyResult);
        }

        if (cloneHierarchy){
            cloneSource.getChildren().forEach(ent -> {
                hyResult.getChildren().add(cloneEntity(ent, true, cloneID));
            });
        }

        return hyResult;
    }

    public void update(float delta){
        //sceneDominion.createScheduler().tick((long) (delta * 1000000));
        //components update first, then entities
        dominionScheduler.tick();
        idToEntityMap.values().forEach(HyEntity::update);
    }

    public HyEntity getCamera(){
        return sceneCamera;
    }
    public void setCamera(HyEntity scCamera){
        this.sceneCamera = scCamera;
    }

    public void createSystem(ECSSystem system){
        dominionScheduler.schedule(system);
        system.hostScene = this;
    }
    public Dominion getSceneDominion(){
        return sceneDominion;
    }
}
