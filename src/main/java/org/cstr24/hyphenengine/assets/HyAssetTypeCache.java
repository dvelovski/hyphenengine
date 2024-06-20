package org.cstr24.hyphenengine.assets;

import org.cstr24.hyphenengine.geometry.HyModel;

import java.util.HashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HyAssetTypeCache<T extends HyAsset> {
    private static final Logger LOGGER = Logger.getLogger(HyAssetTypeCache.class.getName());
    public static final Function<HyAsset, String> DEFAULT_NAME_SUPPLIER = (resource) -> resource.retrievalHandle;

    public HashMap<String, T> resourceMap;
    public HashMap<String, T> customNameMap;

    private Function<HyAsset, String> resourceNameSupplier = DEFAULT_NAME_SUPPLIER;

    public HyAssetTypeCache<T> setResourceNameSupplier(Function<HyAsset, String> supplier){
        this.resourceNameSupplier = supplier;
        return this;
    }

    public void storeResource(HyAsset resource){
        resourceMap.put(resource.retrievalHandle, (T) resource);
        if (resourceNameSupplier != DEFAULT_NAME_SUPPLIER){
            customNameMap.put(resourceNameSupplier.apply(resource), (T) resource);
        }
    }
    public T getResource(String assetName){
        if (customNameMap.containsKey(assetName)){
            return customNameMap.get(assetName);
        }else{
            return resourceMap.get(assetName);
        }
    }
    public boolean resourceLoaded(String assetName){
        return customNameMap.containsKey(assetName) || resourceMap.containsKey(assetName);
    }
    public HyAssetTypeCache(){
        resourceMap = new HashMap<>();
        customNameMap = new HashMap<>();
    }
    public HyAssetTypeCache(Function<HyAsset, String> nameSupplier){
        this();
        setResourceNameSupplier(nameSupplier);
    }
    public void unload(String assetName){
        if (resourceMap.containsKey(assetName)){
            unloadResource(resourceMap.get(assetName));
        }
    }
    public void unload(HyAsset resource){
        unloadResource(resource);
    }

    public void unloadAll() {
        resourceMap.values().forEach(this::unloadResource);
    }
    public void reloadAll(){
        resourceMap.values().forEach(this::reloadResource);
    }

    private void unloadResource(HyAsset res){
        if (res instanceof HyModel){
            //no default models for now
            return;
        }

        if (res.isLoaded() && !res.isResident()){
            res.getUsers().forEach(user -> {
                //my thinking was:
                //if the resource itself stores the asset loader, this could lead to leaks if we subbed out the loader during runtime (would this ever happen anyway?)
                //if the resource itself just knows what type it is, we can get the asset loader to call whatever bound loader is there, then supply default
                user.objRef = AssetLoader.get().getLoader(res.assetType).supplyDefault();
            });
            res.unload();
            res.setLoaded(false);

            LOGGER.log(Level.INFO, "Unloaded asset " + res.retrievalHandle + " / " + res.resourceName);
        }
    }
    private void reloadResource(HyAsset res){
        if (res instanceof HyModel){
            //no default models for now
            return;
        }

        if (res.isLoaded()){
            unloadResource(res);
        }

        //if the resource HAS no users, don't reload it, it'll be reloaded when needed
        if (!res.isResident()){
            //i can't just call reload in here because i could end up polluting the hashmap with references to default assets.
            //i don't want that - i don't want a mapping of 'name' to 'default asset'. i only want 'name' to map to a valid resource.
            //i need a way to force the asset loader to override (ignore anything cached)
            //solution: unload resource, then asset loader will ignore anything in the hashmap
            
            //this will reload and create a handle which is a tad wasteful but fine
            HyAsset newRes = AssetLoader.get().loadResource(res.assetType, res.retrievalHandle).get();

            //in calling this way, 'res' should be removed from the map because it's been replaced.
            //since it's already been unloaded, this 'res' parameter should be the last reference to the object itself.
            //that means 'res' can be let to dangle and be cleaned up by GC.
            res.getUsers().forEach(user -> user.objRef = newRes);
        }
    }
    public void destroyAllResources(){
        for (T res : resourceMap.values()) {
            res.unload();
            LOGGER.log(Level.INFO, "Destroyed asset " + res.retrievalHandle + " / " + res.resourceName);
        }
    }
}
