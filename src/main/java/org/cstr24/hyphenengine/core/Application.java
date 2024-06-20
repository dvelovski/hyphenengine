package org.cstr24.hyphenengine.core;

public abstract class Application {
    public ApplicationSettings settings;
    public ApplicationStartupSettings startupSettings;

    public OSWindow applicationWindow;

    public boolean exitRequested = false;

    public HyGameState state;

    public final void start(){
        initialise();
    }
    public abstract void initialise();
    public abstract void update(float delta);
    public abstract void render(float delta);

    public void exit(){
        exitRequested = true;
    }

    public boolean canShutdown(){
        return true;
    }
    public abstract void shutdown();

    public Application(){
        this(new ApplicationStartupSettings());
        settings = new ApplicationSettings();
        state = new HyGameState();
    }
    public Application(ApplicationStartupSettings settings){
        this.startupSettings = settings;
    }
}
