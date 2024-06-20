package org.cstr24.hyphenengine.scene;

import dev.dominion.ecs.api.Scheduler;

public abstract class ComponentSystem {
    public Scheduler system;
    public abstract void update(float delta);
}
