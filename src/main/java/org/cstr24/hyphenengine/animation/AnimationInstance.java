package org.cstr24.hyphenengine.animation;

import org.cstr24.hyphenengine.entities.components.ModelComponent;

public class AnimationInstance {
    public ModelComponent owner;
    public boolean paused = false;

    public void update(float delta){

    }

    public void toggle() {
        paused = !paused;
    }
    public void step(){

    }
}
