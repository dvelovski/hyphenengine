package org.cstr24.hyphenengine.entities.systems;

import org.cstr24.hyphenengine.scene.Scene;

public abstract class ECSSystem implements Runnable{
    @Override
    public abstract void run();
    public String systemName = "";
    public Scene hostScene;
}
