package org.cstr24.hyphengl.assets;

public class HyResHandle<T extends HyResource> {
    public HyResource objRef;

    public HyResHandle(T ref){
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
