package org.cstr24.hyphengl.assets;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssetCache {
    private static final Logger LOGGER = Logger.getLogger(AssetCache.class.getName());

    private static AssetCache instance;
    private final Map<String, HyResourceCache<?>> registeredCacheProviders;

    static {
        instance = new AssetCache();
    }

    public static AssetCache get() {
        return instance;
    }

    public AssetCache(){
        registeredCacheProviders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void registerCacheProvider(String assetType, HyResourceCache<?> cache){
        registeredCacheProviders.put(assetType, cache);
    }
    public void storeAsset(String assetType, HyResource resource){
        if (registeredCacheProviders.containsKey(assetType)){
            registeredCacheProviders.get(assetType).storeResource(resource);
        }else{
            LOGGER.log(Level.WARNING, "There is no registered cache provider for " + assetType + " that can store resource " + resource);
        }
    }
    public boolean assetCached(String assetType, String name){
        return (registeredCacheProviders.containsKey(assetType) && registeredCacheProviders.get(assetType).resourceLoaded(name));
    }
    public <T extends HyResource> T getCachedAsset(String assetType, String name){
        return (T) registeredCacheProviders.get(assetType).getResource(name);
    }
    public void unloadAllImmediate(){
        registeredCacheProviders.values().forEach(HyResourceCache::unloadAll);
    }
    public void reloadAllImmediate(){
        instance.registeredCacheProviders.values().forEach(HyResourceCache::reloadAll);
    }
    public void destroy(){
        registeredCacheProviders.values().forEach(HyResourceCache::destroyAllResources);
    }
}
