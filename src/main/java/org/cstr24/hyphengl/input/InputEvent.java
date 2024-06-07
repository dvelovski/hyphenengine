package org.cstr24.hyphengl.input;

import org.cstr24.hyphengl.engine.OSWindow;
import org.lwjgl.glfw.GLFW;

public abstract class InputEvent<T> {
    public Source source;
    public Action action;
    public OSWindow originator;

    public boolean shiftHeld;
    public boolean controlHeld;
    public boolean altHeld;
    public boolean superHeld;

    public boolean consumed;

    public abstract void doReset();

    public InputEvent<T> mods(boolean _shift, boolean _ctrl, boolean _alt, boolean _sup){
        this.shiftHeld = _shift;
        this.controlHeld = _ctrl;
        this.altHeld = _alt;
        this.superHeld = _sup;
        return this;
    }
    public InputEvent<T> mods(int glfwEnum){
        this.shiftHeld = (glfwEnum & GLFW.GLFW_MOD_SHIFT) != 0;
        this.controlHeld = (glfwEnum & GLFW.GLFW_MOD_CONTROL) != 0;
        this.altHeld = (glfwEnum & GLFW.GLFW_MOD_ALT) != 0;
        this.superHeld = (glfwEnum & GLFW.GLFW_MOD_SUPER) != 0;

        /*System.out.println("InputEvent.extendedDetails: SH;CT;AL;SU = " +
                shiftHeld + "," +
                controlHeld + "," +
                altHeld + "," +
                superHeld);*/
        return this;
    }

    public enum Source {
        Mouse, Keyboard, ScrollWheel, Char, Joystick
    }

    public enum Action{
        KeyPress, KeyRelease, KeyRepeat,
        MouseEnter, MouseExit, MouseMove, MousePress, MouseRelease, MouseDrag,
        Scroll;

        public static Action valueOf(int glfwEnum){
            switch (glfwEnum){
                case GLFW.GLFW_RELEASE -> {
                    return KeyRelease;
                }
                case GLFW.GLFW_PRESS -> {
                    return KeyPress;
                }
                case GLFW.GLFW_REPEAT -> {
                    return KeyRepeat;
                }
            }
            return null;
        }
    }

    public T from(OSWindow _originator){
        this.originator = _originator;
        return (T) this;
    }

    public void reset(){
        source = null;
        action = null;

        shiftHeld = controlHeld = altHeld = superHeld = false;

        doReset();
    }

    public void consume(){
        this.consumed = true;
    }
    public boolean isConsumed(){
        return this.consumed;
    }
}
