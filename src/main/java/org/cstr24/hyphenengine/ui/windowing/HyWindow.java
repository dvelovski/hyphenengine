package org.cstr24.hyphenengine.ui.windowing;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphenengine.assets.HyAssetHandle;
import org.cstr24.hyphenengine.input.MouseEvent;
import org.cstr24.hyphenengine.ui.css.*;
import org.lwjgl.util.yoga.Yoga;

import java.util.ArrayList;

public class HyWindow extends BaseWindow<HyWindow>{
    private final ArrayList<HyAssetHandle<HyStyleSheet>> styleSheets;
    private final StyleProcessor styleProcessor;
    private final TransitionProcessor transitionProcessor;

    private Panel rootPanel;
    private TitleBar titleBar;
    private Panel content;
    private Button closeButton;

    private boolean styleUpdateNeeded;

    private boolean fullscreen;
    private BaseControl mouseFocussedControl;
    private BaseControl mouseActiveControl;

    public HyWindow(){
        flags |= WINDOW_TITLEBAR;

        styleSheets = new ArrayList<>();
        styleProcessor = new StyleProcessor(this);
        transitionProcessor = new TransitionProcessor(this);

        rootPanel = new Panel();
        rootPanel.setControlID("root");
        rootPanel.setOwnerWindow(this);
        rootPanel.drawBackground = false;
        Yoga.YGNodeStyleSetFlexGrow(rootPanel.nodeHandle, 1);

        titleBar = new TitleBar();
        titleBar.addPseudoClass(PseudoClass.Focus);
        titleBar.setControlID("titlebar");
        titleBar.setOwnerWindow(this);
        //Yoga.YGNodeStyleSetFlexGrow(titleBar.nodeHandle, 1);

        closeButton = new Button();
        closeButton.setStyleClass("closeButton");
        closeButton.setControlID("closebutton");
        closeButton.caption = "X";
        titleBar.addChild(closeButton);

        rootPanel.addChild(titleBar);
    }
    public void addStyleSheet(HyAssetHandle<HyStyleSheet> styleSheetHandle){
        addStyleSheet(styleSheetHandle, -1);
    }
    public void addStyleSheet(HyAssetHandle<HyStyleSheet> styleSheetHandle, int index){
        if (index == -1 || index > styleSheets.size()){
            styleSheets.add(styleSheetHandle);
        }else{
            styleSheets.add(index, styleSheetHandle);
        }
    }
    public ArrayList<HyAssetHandle<HyStyleSheet>> getStyleSheets(){
        return styleSheets;
    }

    @Override
    public void positionChanged(float newX, float newY) {

    }

    @Override
    public void sizeChanged(float newW, float newH) {
        rootPanel.setSizeAbsolute(newW, newH);
        rootPanel.updateLayout();
    }

    @Override
    public void render() {
        rootPanel.beginRender(getPen());
    }

    @Override
    public void update() {
        super.update();

        if (fullscreen){
            windowManager.setFullscreen(this);
        }

        if (styleUpdateNeeded){
            styleUpdate();
            styleUpdateNeeded = false;
        }

        transitionProcessor.update();
    }

    public void styleUpdate(){
        styleProcessor.evaluateStyleSheets();
        rootPanel.updateLayout();
    }
    public void controlClassUpdated(){
        styleUpdateNeeded = true;
    }

    public Panel getRootPanel(){
        return rootPanel;
    }
    public Panel getTitleBar(){
        return titleBar;
    }

    public void setFullscreen(boolean state){
        this.fullscreen = state;
    }
    public boolean isFullscreen(){
        return this.fullscreen;
    }

    public void startControlTransition(BaseControl control, ECSSProperty property, Object targetValue){
        transitionProcessor.addControlTransition(control, property, targetValue);
    }

    public void propagateMouseEvent(MouseEvent mouseEvent){

        if (mouseActiveControl != null){
            mouseActiveControl.propagateMouseEvent(mouseEvent);
        }else{
            if (mouseFocussedControl != null){
                mouseFocussedControl.propagateMouseEvent(mouseEvent);
            }
            rootPanel.propagateMouseEvent(mouseEvent);
        }
    }

    public void controlGainedMouseFocus(BaseControl control){
        mouseFocussedControl = control;
        mouseFocussedControl.addPseudoClass(PseudoClass.Hover);
        //System.out.println("control gained mouse focus: " + control);
    }
    public void controlLostMouseFocus(BaseControl control){
        if (mouseFocussedControl == control){
            mouseFocussedControl.removePseudoClass(PseudoClass.Hover);
            mouseFocussedControl = null;
        }
    }
    public void controlGainedMouseActive(BaseControl control){

    }
    public void controlLostMouseActive(BaseControl control){

    }
}
