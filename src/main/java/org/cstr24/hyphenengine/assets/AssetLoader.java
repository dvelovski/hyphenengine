package org.cstr24.hyphenengine.assets;

import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AssetLoader {
    private static final Logger LOGGER = Logger.getLogger(AssetLoader.class.getName());

    private static AssetLoader instance = null;

    private final Map<String, AResourceLoader<?>> registeredLoaders;
    
    public static AssetLoader get(){
        if (instance == null) {
            instance = new AssetLoader();
        }
        return instance;
    }

    public AssetLoader(){
        registeredLoaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public void registerLoader(String assetType, AResourceLoader<?> loader){
        if (registeredLoaders.containsKey(assetType)){
            LOGGER.log(Level.WARNING, "Unable to register loader - there is already an asset loader registered for " + assetType + ".");
        }else{
            registeredLoaders.put(assetType, loader);
        }
    }
    public void unregisterLoader(String assetType){
        registeredLoaders.remove(assetType);
    }

    public <T extends HyAsset> HyAssetHandle<T> loadResource(String assetType, String retrievalHandle){
        if (registeredLoaders.containsKey(assetType)){
            T resource;
            AResourceLoader<?> loader = registeredLoaders.get(assetType);
            boolean assetCached = AssetCache.get().assetCached(assetType, retrievalHandle);
            boolean assetAvailable = (assetCached && AssetCache.get().getCachedAsset(assetType, retrievalHandle).isLoaded());

            if (assetAvailable){
                resource = AssetCache.get().getCachedAsset(assetType, retrievalHandle);
            }else{
                resource = (T) loader.loadResource(retrievalHandle);
            }

            if (resource != null){
                AssetCache.get().storeAsset(assetType, resource);
            }else{
                LOGGER.log(Level.INFO, "Returning default asset in place of " + retrievalHandle + " as it could not be loaded.");
                resource = (T) loader.supplyDefault();
            }

            return new HyAssetHandle<>(resource);
        }else{
            LOGGER.log(Level.WARNING, "No loader registered for asset type: " + assetType + " - will not be able to load " + retrievalHandle);
            return null;
        }
    }

    public void preload() {
        registeredLoaders.values().forEach(AResourceLoader::preload);

        //manage the default assets as well?
        registeredLoaders.values().forEach(loader -> {
            /*HyResource resource = loader.supplyDefault();
            if (resource != null){
                AssetCache.get().storeAsset(resource.assetType, resource);
            }*/
        });
    }
    public AResourceLoader<?> getLoader(String assetType){
        return registeredLoaders.get(assetType);
    }

    public void update(){
        //tasks
    }
}
