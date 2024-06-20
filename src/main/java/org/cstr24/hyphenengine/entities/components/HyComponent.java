package org.cstr24.hyphenengine.entities.components;

import org.cstr24.hyphenengine.entities.HyEntity;

public abstract class HyComponent {
    public static final String BASE_NAMESPACE = "hyphen";
    public static final String composeTypeName(String... components){
        return String.join(".", components);
    }

    /** our entity owner **/
    private HyEntity owner;

    public void setOwner(HyEntity ent){
        this.owner = ent;
    }
    public HyEntity getEntity(){
        return owner;
    }

    public abstract String getComponentType();
    public abstract String getComponentSimpleName();
    public abstract <T extends HyComponent> T reset();
    public abstract <T extends HyComponent> T cloneComponent();
    public abstract <T extends HyComponent> T create();
}
