package org.cstr24.hyphengl.ui.windowing;

import imgui.ImGui;

/**
 * A window rendered using ImGUI.
 */
public abstract class ImmediateUIWindow extends BaseWindow<ImmediateUIWindow>{

    public ImmediateUIWindow() {

    }

    public void update(){

    }

    @Override
    public void render() {
        if (ImGui.begin(this.caption + "##" + this.index)){
            immediateModeRender();
        }
        ImGui.end();
    }
    public abstract void immediateModeRender();

    @Override
    public void positionChanged(float newX, float newY) {
        //NOP for immediate windows
    }

    @Override
    public void sizeChanged(float newW, float newH) {
        //NOP for immediate windows
    }
}
