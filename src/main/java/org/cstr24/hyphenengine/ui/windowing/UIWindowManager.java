package org.cstr24.hyphenengine.ui.windowing;

import imgui.ImGui;
import org.cstr24.hyphenengine.assets.AssetLoader;
import org.cstr24.hyphenengine.assets.HyAssetHandle;
import org.cstr24.hyphenengine.input.*;
import org.cstr24.hyphenengine.ui.css.HyStyleSheet;

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
    private HyWindow focusedHyWindow;

    /** State for ImGUI **/
    private boolean imGUILayerEnabled;

    private long nvgContext;
    private float hostWindowW;
    private float hostWindowH;

    private int windowIDCounter;

    public UIWindowManager(){
        immediateWindows = new ArrayList<>();
        hyWindows = new ArrayList<>();

        /*CSSShortHandRegistry.registerShortHandDescriptor(
            new CSSShortHandDescriptor(
                ECSSProperty.TRANSITION,
                new CSSPropertyWithDefaultValue(CCSSProperties.TRANSITION_DELAY, "0s"),
                new CSSPropertyWithDefaultValue(CCSSProperties.TRANSITION_DURATION, "0s"),
                new CSSPropertyWithDefaultValue(CCSSProperties.TRANSITION_PROPERTY, CCSSValue.ALL),
                new CSSPropertyWithDefaultValue(CCSSProperties.TRANSITION_TIMING_FUNCTION, "ease")
            )
        );*/
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

        HyAssetHandle<HyStyleSheet> result = AssetLoader.get().loadResource(HyStyleSheet.RESOURCE_TYPE, "res/ui/basestyles.css");
        window.addStyleSheet(result, 0); //add it to the top
        window.styleUpdate();
    }
    public void openWindow(ImmediateUIWindow window){
        immediateWindows.add(window);

        window.setIndex(windowIDCounter++);
        window.setWindowManager(this);

    }
    public void init(){
        InputManager.registerEventConsumer(this);
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
                return consumeMouseEvent((MouseEvent) event);
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
    public boolean consumeMouseEvent(MouseEvent event){
        boolean result = false;
        boolean imGuiResult = (imGUILayerEnabled ? ImGui.getIO().getWantCaptureMouse() : false);

        if (imGuiResult){
            result = imGuiResult;
        }else{
            result = propagateMouseEvent(event);
        }

        return result;
    }

    public boolean propagateMouseEvent(MouseEvent event){
        //mouse behaviours:
        //hover
        //mouse enter / exit
        //window to know which control has mouse focus, rather, control requests mouse focus
        //click
        //i think first and foremost we want to check with the focused window if it needs to do anything
        //then we can send the event to
        //let's figure out which window this event falls into first


        hyWindows.forEach(window -> {
            if (event.mouseX >= window.position.x && event.mouseX <= window.position.x + window.size.x){
                if (event.mouseY >= window.position.y && event.mouseY <= window.position.y + window.size.y){
                    window.propagateMouseEvent(event);
                }
            }
        });

        return false;
    }

    public void update(){
        hyWindows.forEach(HyWindow::update);
        immediateWindows.forEach(ImmediateUIWindow::update);
    }

    public void setFullscreen(HyWindow window){
        window.setSize(hostWindowW, hostWindowH);
    }
    public void setHostWindowSize(float w, float h){
        this.hostWindowW = w;
        this.hostWindowH = h;
    }
}
