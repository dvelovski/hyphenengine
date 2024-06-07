package org.cstr24.hyphengl.entities.components;

import org.cstr24.hyphengl.entities.Entity;

public abstract class Component<T extends Component<?>> {
    /** our entity owner **/
    private Entity<?> owner;

    public void setOwner(Entity<?> ent){
        this.owner = ent;
    }
    public Entity<?> getEntity(){
        return owner;
    }

    public abstract String getComponentType();
    public abstract String getComponentSimpleName();
    public abstract T reset();
    public abstract T cloneComponent();
    public abstract T create();

    public abstract void update(float delta);
}
