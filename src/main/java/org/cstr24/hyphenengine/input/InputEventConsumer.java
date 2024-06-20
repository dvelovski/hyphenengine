package org.cstr24.hyphenengine.input;

public abstract class InputEventConsumer {
    private boolean enabled;

    public abstract boolean consume(InputEvent<?> event);
    public boolean isEnabled(){
        return enabled;
    }
    public void setInputConsumerEnabled(boolean state){
        enabled = state;
    }
}
