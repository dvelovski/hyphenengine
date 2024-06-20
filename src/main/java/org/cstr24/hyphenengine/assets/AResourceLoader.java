package org.cstr24.hyphenengine.assets;

import java.nio.ByteBuffer;

public abstract class AResourceLoader<T extends HyAsset> {
    /**
     * Loads a resource located by the specified handle.
     * This is generally a file name, but it can be any other internal identifier as well if you want.
     * @param handle The handle with which the resource will be located.
     * @return A resource, or a fallback object.
     */
    public abstract T loadResource(String handle);

    /**
     * Runs any specific setup code required by the loader. This could include loading default assets or initializing libraries.
     */
    public abstract void preload();
    public abstract void unloadDefaults();

    /**
     * Requests that the loader provide a default object.
     * @return A default object, if the loader specifies one.
     */
    public abstract T supplyDefault();

    public abstract AssetLoadTask beginAssetLoad();
    public abstract void mainAssetLoad(AssetLoadTask aTask);

    public abstract void setAssetContents(ByteBuffer source);
    public abstract void setAssetContents(T source);
}
