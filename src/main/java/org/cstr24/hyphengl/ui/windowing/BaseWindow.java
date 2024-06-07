package org.cstr24.hyphengl.ui.windowing;

import org.cstr24.hyphengl.ui.Pen;
import org.joml.Vector2f;

import java.util.ArrayList;

public abstract class BaseWindow<T extends BaseWindow<T>> {
    public static final int WINDOW_TITLEBAR = 00000001;

    //position
    //name
    //caption
    //owner

    public final Vector2f position;
    public final Vector2f size;
    public String name;
    public String caption;
    public BaseWindow<?> owner;

    public UIWindowManager windowManager;
    public int index;

    public int flags;

    public final ArrayList<BaseWindow<?>> children = new ArrayList<>();

    public BaseWindow(){
        name = "";
        caption = "";

        position = new Vector2f();
        size = new Vector2f();

        owner = null;

        index = 0;
        flags = 0;
    }

    public T setIndex(int newIndex){
        this.index = newIndex;
        return (T) this;
    }

    public T setPosition(float posX, float posY){
        position.set(posX, posY);
        positionChanged(posX, posY);
        return (T) this;
    }
    public abstract void positionChanged(float newX, float newY);

    public T setSize(float newW, float newH){
        size.set(newW, newH);
        sizeChanged(newW, newH);
        return (T) this;
    }
    public abstract void sizeChanged(float newW, float newH);

    public T setCaption(String newCaption){
        this.caption = newCaption;
        return (T) this;
    }

    public T setName(String newName){
        this.name = newName;
        return (T) this;
    }

    public T setOwner(BaseWindow<?> newOwner){
        this.owner = newOwner;
        return (T) this;
    }

    public T setWindowManager(UIWindowManager wManager){
        this.windowManager = wManager;
        return (T) this;
    }

    public abstract void render();
    public Pen getPen(){
        return new Pen(windowManager.getNVGContext());
    }

    public void update(){

    }
}
