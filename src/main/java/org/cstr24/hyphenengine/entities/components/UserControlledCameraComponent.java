package org.cstr24.hyphenengine.entities.components;

public class UserControlledCameraComponent extends HyComponent{
    public static final String TYPE = composeTypeName(BASE_NAMESPACE, "usercameracomponent");

    @Override
    public String getComponentType() {
        return TYPE;
    }

    @Override
    public String getComponentSimpleName() {
        return "User's Camera Component";
    }

    @Override
    public UserControlledCameraComponent reset() {
        return this;
    }

    @Override
    public UserControlledCameraComponent cloneComponent() {
        return null;
    }

    @Override
    public UserControlledCameraComponent create() {
        return this;
    }
}
