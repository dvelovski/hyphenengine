package org.cstr24.hyphengl.ui.windowing;

import org.cstr24.hyphengl.assets.HyResHandle;
import org.cstr24.hyphengl.ui.css.HyCStyleSheet;
import org.cstr24.hyphengl.ui.css.PseudoClass;
import org.cstr24.hyphengl.ui.css.StyleProcessor;

import java.util.ArrayList;

public class HyWindow extends BaseWindow<HyWindow>{
    private final ArrayList<HyResHandle<HyCStyleSheet>> styleSheets;
    private final StyleProcessor styleProcessor;

    private Panel rootPanel;
    private TitleBar titleBar;
    private Panel content;
    private Button closeButton;

    private boolean styleUpdateNeeded;


    public HyWindow(){
        flags |= WINDOW_TITLEBAR;

        styleSheets = new ArrayList<>();
        styleProcessor = new StyleProcessor(this);

        rootPanel = new Panel();
        rootPanel.setControlID("root");
        rootPanel.setOwnerWindow(this);

        titleBar = new TitleBar();
        titleBar.addPseudoClass(PseudoClass.Focus);

        closeButton = new Button();
        closeButton.setStyleClass("closeButton");
        closeButton.caption = "X";
        titleBar.addChild(closeButton);

        content = new Panel();
        content.addStyleClass("contentPanel");

        rootPanel.addChild(titleBar);
        rootPanel.addChild(content);
    }
    public void addStyleSheet(HyResHandle<HyCStyleSheet> styleSheetHandle){
        addStyleSheet(styleSheetHandle, -1);
    }
    public void addStyleSheet(HyResHandle<HyCStyleSheet> styleSheetHandle, int index){
        if (index == -1 || index > styleSheets.size()){
            styleSheets.add(styleSheetHandle);
        }else{
            styleSheets.add(index, styleSheetHandle);
        }
    }
    public ArrayList<HyResHandle<HyCStyleSheet>> getStyleSheets(){
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
        if (styleUpdateNeeded){
            styleUpdate();
            styleUpdateNeeded = false;
        }
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
    public Panel getContentPanel(){
        return content;
    }
    public Panel getTitleBar(){
        return titleBar;
    }
}
