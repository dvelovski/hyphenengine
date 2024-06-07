package org.cstr24.hyphengl.input;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;
import org.lwjgl.glfw.GLFW;

public class MouseEvent extends InputEvent<MouseEvent>{
    public int mouseX;
    public int mouseY;

    public Button button;

    public MouseEvent(int _act, int _mX, int _mY){
        this(Action.valueOf(_act), _mX, _mY, Button.None);
    }
    public MouseEvent(int _act, int _mX, int _mY, Button _bt){
        this(Action.valueOf(_act), _mX, _mY, _bt);
    }
    public MouseEvent(Action _act, int _mX, int _mY){
        this(_act, _mX, _mY, Button.None);
    }
    public MouseEvent(Action _act, int _mX, int _mY, Button _bt){
        this.action = _act;
        this.mouseX = _mX;
        this.mouseY = _mY;
        this.button = _bt;
        this.source = Source.Mouse;
    }
    @Override
    public void doReset() {

    }

    public enum Button {
        Left(ImGuiMouseButton.Left), Middle(ImGuiMouseButton.Middle), Right(ImGuiMouseButton.Right), None(-1);

        public final int imGuiConstant;

        Button(int c){
            imGuiConstant = c;
        }
        public static Button valueOf(int glfwEnum){
            switch (glfwEnum){
                case GLFW.GLFW_MOUSE_BUTTON_LEFT -> {
                    return Left;
                }
                case GLFW.GLFW_MOUSE_BUTTON_RIGHT -> {
                    return Right;
                }
                case GLFW.GLFW_MOUSE_BUTTON_MIDDLE -> {
                    return Middle;
                }
            }
            return null;
        }
    }
}
