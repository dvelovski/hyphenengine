package org.cstr24.hyphengl.ui.windowing;

import imgui.ImGui;
import org.cstr24.hyphengl.assets.AssetLoader;
import org.cstr24.hyphengl.assets.HyResHandle;
import org.cstr24.hyphengl.engine.OSWindow;
import org.cstr24.hyphengl.input.*;
import org.cstr24.hyphengl.ui.css.HyCStyleSheet;

import java.util.ArrayList;

public class UIWindowManager extends InputEventConsumer {
    private static UIWindowManager instance;
    public static UIWindowManager get(){
        if (instance == null) {
            instance = new UIWindowManager();
        }
        return instance;
    }

    /** Open windows **/
    public final ArrayList<ImmediateUIWindow> immediateWindows;
    public final ArrayList<HyWindow> hyWindows;

    /** State for ImGUI **/
    private int lastMouseX;
    private int lastMouseY;
    private boolean imGUILayerEnabled;

    private long nvgContext;

    private int windowIDCounter;

    public UIWindowManager(){
        immediateWindows = new ArrayList<>();
        hyWindows = new ArrayList<>();
    }
    public void setNVGContext(long ctx){
        this.nvgContext = ctx;
    }
    public long getNVGContext(){
        return this.nvgContext;
    }

    public void setImGUILayerEnabled(boolean state){
        imGUILayerEnabled = state;
    }

    public void openWindow(HyWindow window){
        hyWindows.add(window);
        window.setIndex(windowIDCounter++);
        window.setWindowManager(this);

        HyResHandle<HyCStyleSheet> result = AssetLoader.get().loadResource(HyCStyleSheet.RESOURCE_TYPE, "res/ui/basestyles.css");
        window.addStyleSheet(result, 0); //add it to the top
        window.styleUpdate();
    }
    public void openWindow(ImmediateUIWindow window){
        immediateWindows.add(window);

        window.setIndex(windowIDCounter++);
        window.setWindowManager(this);

    }
    public void init(){
        InputManager.get().registerEventConsumer(this);
    }

    public void renderHyWindows(){
        hyWindows.forEach(HyWindow::render);
    }
    public void renderImmediateUI(){
        immediateWindows.forEach(ImmediateUIWindow::render);
    }

    @Override
    public boolean consume(InputEvent<?> event) {
        switch (event.source){
            case Keyboard -> {
                return consumeKeyboardEvent();
            }
            case Mouse -> {
                return consumeMouseEvent();
            }
        }
        return false;
    }
    public boolean consumeKeyboardEvent(){
        boolean result;
        boolean imGuiResult = (imGUILayerEnabled ? ImGui.getIO().getWantCaptureKeyboard() : false);

        result = imGuiResult;

        return result;
    }
    public boolean consumeMouseEvent(){
        boolean result;
        boolean imGuiResult = (imGUILayerEnabled ? ImGui.getIO().getWantCaptureMouse() : false);

        result = imGuiResult;

        return result;
    }
    public void update(){
        hyWindows.forEach(HyWindow::update);
        immediateWindows.forEach(ImmediateUIWindow::update);
    }

}
