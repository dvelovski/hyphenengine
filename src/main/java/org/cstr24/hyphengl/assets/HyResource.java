package org.cstr24.hyphengl.assets;

import java.util.ArrayList;

public abstract class HyResource {
    public String resourceName = ""; //the name of this resource
    public String retrievalHandle = ""; //resource path used to load this resource

    public String assetType = "";

    //should this resource NEVER be unloaded?
    private boolean resident;
    private boolean loaded;

    //that way if it's unloaded but we still have active handles, we can get the loader to supply a default to those handles.

    //those handles are then going to be utterly screwed and unrecoverable if 'this' asset is then retrieved.
    //does that mean, once an asset is unloaded, we keep a reference to it around in memory, delete the actual data via the 'unload', set its handles to defaults, then if it's reloaded, for any handles, set them again?
    //i like that idea.

    //what i should do is a test where i can hit a button to nuke and unload everything
    //then another to re-load everything via its retrieval handle, that's going to be interesting
    //loaders can override 'internal' names so we don't hit the asset cache?

    private final ArrayList<HyResHandle<?>> userHandles;

    private int scope;

    public HyResource(){
        userHandles = new ArrayList<>();
    }

    public void addUser(HyResHandle<?> handle){
        userHandles.add(handle);
    }

    public void removeUser(HyResHandle<?> handle){
        userHandles.remove(handle);
    }

    public void setScope(int newScope) {
        this.scope = newScope;
    }
    public int getScope(){
        return scope;
    }

    public abstract void unload();
    public ArrayList<HyResHandle<?>> getUsers(){
        return userHandles;
    }

    public <T extends HyResource> T setResident(){
        this.resident = true;
        return (T) this;
    }
    public boolean isResident(){
        return resident;
    }
    public void setLoaded(boolean state){
        this.loaded = state;
    }
    public boolean isLoaded(){
        return loaded;
    }

    public void setAssetType(String type) {
        this.assetType = type;
    }

    public void setRetrievalHandle(String handle) {
        this.retrievalHandle = handle;
    }
    public void setName(String newName){
        this.resourceName = newName;
    }
}
