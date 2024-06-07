package org.cstr24.hyphengl.engine;

import org.cstr24.hyphengl.scene.World;

public abstract class Application {
    public ApplicationSettings settings;
    public ApplicationStartupSettings startupSettings;

    public OSWindow applicationWindow;

    public boolean exitRequested = false;

    public World currentScene;

    public final void start(){
        initialise();
    }
    public abstract void initialise();
    public abstract void update();
    public abstract void render();

    public void exit(){
        exitRequested = true;
    }

    public boolean canShutdown(){
        return true;
    }
    public abstract void shutdown();

    public Application(){
        settings = new ApplicationSettings();
        startupSettings = new ApplicationStartupSettings();
    }
}
