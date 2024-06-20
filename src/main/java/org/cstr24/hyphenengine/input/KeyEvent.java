package org.cstr24.hyphenengine.input;

import org.lwjgl.glfw.GLFW;

public class KeyEvent extends InputEvent<KeyEvent> {
    public int keyCode = -1;

    public KeyEvent(int _act, int _keyCode){
        this(Action.valueOf(_act), _keyCode);
    }
    public KeyEvent(Action _act, int _keyCode) {
        this.source = Source.Keyboard;
        this.action = _act;
        this.keyCode = _keyCode;
    }

    public String getKeyName(){
        return GLFW.glfwGetKeyName(keyCode, 0);
    }

    @Override
    public void innerReset() {
        keyCode = -1;
    }
}
