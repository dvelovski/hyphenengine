package org.cstr24.hyphenengine.assets;

public class HyAssetHandle<T extends HyAsset> {
    public HyAsset objRef;

    public HyAssetHandle(T ref){
        this.objRef = ref;
        objRef.addUser(this);
    }

    public void release(){
        objRef.removeUser(this);
    }

    public T get(){
        return (T) objRef;
    }
}
