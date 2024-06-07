package org.cstr24.hyphengl.animation;

import org.cstr24.hyphengl.entities.components.ModelComponent;

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
