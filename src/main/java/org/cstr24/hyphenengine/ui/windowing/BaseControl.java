package org.cstr24.hyphenengine.ui.windowing;

import com.helger.css.ECSSUnit;
import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphenengine.input.MouseEvent;
import org.cstr24.hyphenengine.ui.Pen;
import org.cstr24.hyphenengine.ui.css.*;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.BiConsumer;

import static org.lwjgl.util.yoga.Yoga.*;

public abstract class BaseControl {
    public long nodeHandle;
    BaseControl parent;

    CoordinateType xAxisPositionType;
    CoordinateType yAxisPositionType;

    HorizontalAnchor anchorX;
    VerticalAnchor anchorY;

    OverflowMode overflowX;
    OverflowMode overflowY;

    CoordinateType xAxisSizing;
    CoordinateType yAxisSizing;

    AlignValue alignContent;
    AlignValue alignSelf;

    float preferredX;
    float preferredY;
    float preferredW;
    float preferredH;

    float computedX;
    float computedY;
    float computedW;
    float computedH;

    private ArrayList<String> styleClasses;
    private ArrayList<String> currentPseudoClasses;
    private final HashMap<ECSSProperty, StyleProperty<?>> styleProperties;
    private final HashMap<ECSSProperty, BiConsumer<BaseControl, Object>> stylePropertyChangeListeners;

    private final HashMap<ECSSProperty, TransitionDefinition> transitionDefinitions;
    private final HashMap<ECSSProperty, TransitionInstance> transitionInstances;

    private String baseObjectStyleType = "";

    /** Unique identifier for this control, for lookups, also for styling **/
    private String controlID = "";

    private HyWindow ownerWindow;
    private final ArrayList<BaseControl> children;

    public boolean enabled;
    public boolean visible;

    public boolean drawBackground = true;

    private boolean mouseFocused = false;

    public BaseControl(){
        nodeHandle = YGNodeNew();

        styleClasses = new ArrayList<>();
        currentPseudoClasses = new ArrayList<>();
        styleProperties = new HashMap<>();
        stylePropertyChangeListeners = new HashMap<>();

        transitionDefinitions = new HashMap<>();
        transitionInstances = new HashMap<>();

        setBaseStyleChangeListeners();

        parent = null;

        anchorX = HorizontalAnchor.Left;
        anchorY = VerticalAnchor.Top;

        overflowX = OverflowMode.Constrain;
        overflowY = OverflowMode.Constrain;

        xAxisSizing = CoordinateType.Absolute;
        yAxisSizing = CoordinateType.Absolute;

        preferredX = preferredY = preferredW = preferredH = 0f;
        computedX = computedY = computedW = computedH = 0f;

        controlID = "";

        ownerWindow = null;
        children = new ArrayList<>();

        enabled = true;
        visible = true;
    }

    private void setBaseStyleChangeListeners(){
        stylePropertyChangeListeners.put(ECSSProperty.ALIGN_SELF, (control, val) -> {
            if (val.equals("flex-end")){
                YGNodeStyleSetAlignSelf(this.nodeHandle, YGAlignFlexEnd);
            }
        });
    }

    public StyleProperty<?> getStyleProperty(ECSSProperty key){
        if (styleProperties.containsKey(key)){
            return styleProperties.get(key);
        }else{
            if (parent != null){
                return parent.getStyleProperty(key);
            }
        }
        return null;
    }

    /** EVENTS - override as needed **/

    public void click(){

    }
    public void doubleClick(){

    }
    public void drag(){

    }
    public void mouseDown(){

    }
    public void mouseUp(){

    }
    public void mouseEnter(){

    }
    public void mouseExit(){

    }
    public void focusGained(){

    }
    public void focusLost(){

    }

    public void positionChanged(){

    }
    public void sizeChanged(){

    }

    public void parentChanged(BaseControl newParent){

    }

    /** Member manipulation **/
    public void addChild(BaseControl child){
        addChild(child, -1);
    }
    public void addChild(BaseControl child, int index){
        if (!children.contains(child) && child != this){
            child.setParent(this);

            int effectiveIndex = index;
            int childCount = YGNodeGetChildCount(nodeHandle);

            if (index == -1){
                effectiveIndex = childCount;
            }else if (index > childCount){
                effectiveIndex = childCount;
            }

            YGNodeInsertChild(nodeHandle, child.nodeHandle, effectiveIndex);
            children.add(effectiveIndex, child);

            updateLayout();
        }

    }

    public void removeChild(BaseControl child){
        if (children.contains(child)){
            child.removeChild(child);
            child.setParent(null);

            int nodeIndex = children.indexOf(child);
            if (nodeIndex > -1){
                YGNodeRemoveChild(nodeHandle, nodeIndex);
            }
        }
    }

    public void setParent(BaseControl newParent){
        this.parent = newParent;
        this.ownerWindow = newParent.ownerWindow;

        if (ownerWindow != null){
            ownerWindow.controlClassUpdated();
        }

        parentChanged(newParent);
    }

    public void setOwnerWindow(HyWindow newWindow){
        this.ownerWindow = newWindow;
        //all children need to know
        for (BaseControl c : children){
            c.setOwnerWindow(newWindow);
        }
        //ownerWindowChanged();
    }

    public void updateLayout(){
        float cX, cY;
        cX = switch (overflowX) {
            case Hidden, Scrollbar -> YGUndefined;
            default -> preferredW;
        };
        cY = switch (overflowY) {
            case Hidden, Scrollbar -> YGUndefined;
            default -> preferredH;
        };

        YGNodeCalculateLayout(nodeHandle, cX, cY, YGFlexDirectionColumn);
        layoutUpdated();
    }
    public void layoutUpdated(){
        float nodeLeft = YGNodeLayoutGetLeft(nodeHandle); //X in CSS terminology
        float nodeTop = YGNodeLayoutGetTop(nodeHandle); //Y in CSS terminology
        float nodeW = YGNodeLayoutGetWidth(nodeHandle);
        float nodeH = YGNodeLayoutGetHeight(nodeHandle);

        children.forEach(BaseControl::layoutUpdated);

        if (nodeLeft != computedX || nodeTop != computedY){
            positionChanged();
        }
        computedX = nodeLeft;
        computedY = nodeTop;

        if (nodeW != computedW || nodeH != computedH){
            sizeChanged();
        }
        computedW = nodeW;
        computedH = nodeH;
    }

    /** Instance attributes **/
    public String getControlID(){
        return controlID;
    }

    public void setControlID(String newName){
        newName = newName.startsWith("#") ? newName.substring(1) : newName;
        this.controlID = newName;

        if (ownerWindow != null){
            ownerWindow.controlClassUpdated();
        }
    }

    public boolean isEnabled(){
        return enabled;
    }
    public boolean isVisible(){
        return visible;
    }

    /**
     * Return computed size of this control
     **/
    public void getSize(){

    }

    /** Return computed width of this control  **/
    public float getWidth(){
        return computedW;
    }

    /** Return computed height of this control **/
    public float getHeight(){
        return computedH;
    }

    public float getPreferredWidth(){
        return this.preferredW;
    }

    public float getPreferredHeight(){
        return this.preferredH;
    }

    public void setSize(float newW, float newH){
        switch (xAxisSizing){
            case Absolute -> nSetWidthAbsolute(newW);
            case Auto -> YGNodeStyleSetWidthAuto(nodeHandle);
            case Percentage -> nSetWidthPercent(newW);
        }
        preferredW = newW; //this is the input to preferredW which may or may not be pixels. if absolute, yes. if percentage, no.

        switch (yAxisSizing){
            case Absolute -> nSetHeightAbsolute(newH);
            case Auto -> YGNodeStyleSetHeightAuto(nodeHandle);
            case Percentage -> nSetHeightPercent(newH);
        }
        preferredH = newH;
    }

    public void setSizePercentage(float percW, float percH){
        setSizingMethodW(CoordinateType.Percentage);
        setSizingMethodH(CoordinateType.Percentage);

        nSetWidthPercent(percW);
        nSetHeightPercent(percH);

        preferredW = percW;
        preferredH = percH;
    }

    private void nSetWidthPercent(float percW) {
        YGNodeStyleSetWidthPercent(nodeHandle, percW);
    }
    private void nSetHeightPercent(float percH) {
        YGNodeStyleSetHeightPercent(nodeHandle, percH);
    }

    public void setSizeAuto(){
        setSizingMethodW(CoordinateType.Auto);
        setSizingMethodH(CoordinateType.Auto);
    }

    public void setWidthPercentage(float percW){
        setSizingMethodW(CoordinateType.Percentage);
        nSetWidthPercent(percW);
        preferredW = percW;
    }
    public void setHeightPercentage(float percH){
        setSizingMethodH(CoordinateType.Percentage);
        nSetHeightPercent(percH);
        preferredH = percH;
    }

    private void nSetWidthAbsolute(float absW) {
        YGNodeStyleSetWidth(nodeHandle, absW);
    }
    private void nSetHeightAbsolute(float absH) {
        YGNodeStyleSetHeight(nodeHandle, absH);
    }

    public void setSizeAbsolute(float absW, float absH){
        setWidthAbsolute(absW);
        setHeightAbsolute(absH);;
    }

    public void setWidthAbsolute(float absW){
        this.setSizingMethodW(CoordinateType.Absolute);
        this.preferredW = absW;
        nSetWidthAbsolute(absW);
    }

    public void setHeightAbsolute(float absH) {
        this.setSizingMethodH(CoordinateType.Absolute);
        this.preferredH = absH;
        nSetHeightAbsolute(absH);
    }

    /** Return computed position of this control **/
    public Vector2f getPosition(){
        return getPosition(new Vector2f());
    }
    public Vector2f getPosition(Vector2f dest){
        return dest.set(getX(), getY());
    }

    public float getX(){
        return computedX;
    }

    public float getY(){
        return computedY;
    }

    /**
     * Return preferred position - this is the position we provide in setPosition.
     * Due to the nature of the layout engine the computed position and the preferred position aren't always going to be the same.
     **/
    public Vector2f getPreferredPosition(){
        return getPreferredPosition(new Vector2f());
    }
    public Vector2f getPreferredPosition(Vector2f dest){
        return dest.set(getPreferredX(), getPreferredY());
    }

    public float getPreferredX(){
        return this.preferredX;
    }

    public float getPreferredY(){
        return this.preferredY;
    }
    public void setPosition(float newX, float newY){
        //what we need is to specify where we want to go then mark that a new layout is needed
        //todo position can be percentage as well
        switch (anchorX){
            case Left -> YGNodeStyleSetPosition(nodeHandle, YGEdgeLeft, newX);
            case Right -> YGNodeStyleSetPosition(nodeHandle, YGEdgeRight, newX);
        }
        preferredX = newX;

        switch (anchorY){
            case Top -> YGNodeStyleSetPosition(nodeHandle, YGEdgeTop, newY);
            case Bottom -> YGNodeStyleSetPosition(nodeHandle, YGEdgeBottom, newY);
        }
        preferredY = newY;

        //need to tell my parent i need re-layout
    }
    public void setPosition(Vector2f newPosition){
        //what we need is a layout
        setPosition(newPosition.x, newPosition.y);
    }
    public void setHorizontalAnchor(HorizontalAnchor anchor){
        this.anchorX = anchor;
    }
    public void setVerticalAnchor(VerticalAnchor anchor){
        this.anchorY = anchor;
    }
    public void setOverflowX(OverflowMode overflow){
        this.overflowX = overflow;

        /**
        switch (this.overflowX){
            case Hidden -> controlNode.setOverflow(YogaOverflow.HIDDEN);
            case Scrollbar -> controlNode.setOverflow(YogaOverflow.SCROLL);
            default -> controlNode.setOverflow(YogaOverflow.VISIBLE);
        }
        **/
    }
    public void setOverflowY(OverflowMode overflow){
        this.overflowY = overflow;
    }
    public void setSizingMethodW(CoordinateType method){
        this.xAxisSizing = method;
    }

    public void setSizingMethodH(CoordinateType method){
        this.yAxisSizing = method;
    }

    /** Interaction **/
    public void requestFocus(){

    }

    /** Rendering **/
    public void beginRender(Pen p){

        render(p);
        //draw children
        //clone before this loop, then reset state to here each render
        children.stream().filter(BaseControl::isVisible).forEach(control -> {
            var priorState = p.cloneState();
            p.getTransform().position.add(this.getX(), this.getY());
            control.beginRender(p);
            p.setState(priorState);
        });

        //p.setStrokeColour(Colours.Red);
        //p.drawRectangle(computedX, computedY, computedW, computedH);
    }

    public void render(Pen p){

    }
    public void renderBorder(Pen p){
        //determine if we're rounded
        float[] radii = new float[4];
        boolean rounded = determineIfRounded(radii);

        if (rounded){
            if (determineIfUniformRounding(radii)){
                p.drawRoundRectangle(computedX, computedY, computedW, computedH, radii[0]);
            }else{
                p.drawRoundRectangle(computedX, computedY, computedW, computedH, radii[0], radii[1], radii[2], radii[3]);
            }
        }else{
            p.drawRectangle(computedX, computedY, computedW, computedH);
        }
    }
    public void renderBackground(Pen p){
        //determine if we're rounded
        float[] radii = new float[4];
        boolean rounded = determineIfRounded(radii);

        if (rounded){
            if (determineIfUniformRounding(radii)){
                p.fillRoundRectangle(computedX, computedY, computedW, computedH, radii[0]);
            }else{
                p.fillRoundRectangle(computedX, computedY, computedW, computedH, radii[0], radii[1], radii[2], radii[3]);
            }
        }else{
            p.fillRectangle(computedX, computedY, computedW, computedH);
        }
    }

    private boolean determineIfRounded(float[] radiiIn){
        float[] radii = getBorderRadii();
        System.arraycopy(radii, 0, radiiIn, 0, radii.length);

        for (int i = 0; i < radii.length; i++){
            if (radii[i] > 0){
                return true;
            }
        }
        return false;
    }
    private boolean determineIfUniformRounding(float[] radii){
        float val = radii[0];
        for (int i = 1; i < radii.length; i++){
            if (radii[i] != val){
                return false;
            }
        }
        return true;
    }

    private float[] getBorderRadii(){
        double borderVal = 0f;

        double borderTL = 0, borderTR = 0, borderBR = 0, borderBL = 0;

        if (styleProperties.containsKey(ECSSProperty.BORDER_RADIUS)){
            StyleProperty<Double> pBorderRadius = getProperty(ECSSProperty.BORDER_RADIUS);

            borderVal = pBorderRadius.getEffectiveValue();
            if (pBorderRadius.getUnit() == ECSSUnit.PERCENTAGE){
                borderVal = (borderVal / 100) * computedW;
            }

            borderTL = borderVal;
            borderTR = borderVal;
            borderBR = borderVal;
            borderBL = borderVal;
        }
        if (styleProperties.containsKey(ECSSProperty.BORDER_TOP_LEFT_RADIUS)){
            StyleProperty<Double> prop = getProperty(ECSSProperty.BORDER_TOP_LEFT_RADIUS);
            borderTL = prop.getEffectiveValue();
            if (prop.getUnit() == ECSSUnit.PERCENTAGE){
                borderTL = (borderTL / 100) * computedW;
            }
        }
        if (styleProperties.containsKey(ECSSProperty.BORDER_TOP_RIGHT_RADIUS)){
            StyleProperty<Double> prop = getProperty(ECSSProperty.BORDER_TOP_RIGHT_RADIUS);
            borderTR = prop.getEffectiveValue();
            if (prop.getUnit() == ECSSUnit.PERCENTAGE){
                borderTR = (borderTR / 100) * computedW;
            }
        }
        if (styleProperties.containsKey(ECSSProperty.BORDER_BOTTOM_RIGHT_RADIUS)){
            StyleProperty<Double> prop = getProperty(ECSSProperty.BORDER_BOTTOM_RIGHT_RADIUS);
            borderBR = prop.getEffectiveValue();
            if (prop.getUnit() == ECSSUnit.PERCENTAGE){
                borderBR = (borderBR / 100) * computedW;
            }
        }
        if (styleProperties.containsKey(ECSSProperty.BORDER_BOTTOM_LEFT_RADIUS)){
            StyleProperty<Double> prop = getProperty(ECSSProperty.BORDER_BOTTOM_LEFT_RADIUS);
            borderBL = prop.getEffectiveValue();
            if (prop.getUnit() == ECSSUnit.PERCENTAGE){
                borderBL = (borderBL / 100) * computedW;
            }
        }

        return new float[]{(float) borderTL, (float) borderTR, (float) borderBR, (float) borderBL};
    }

    public HyWindow getOwnerWindow() {
        return ownerWindow;
    }

    /**
     * Sets this control's style class
     * @param className
     */
    public void setStyleClass(String className){
        styleClasses.clear();
        if (className.contains(" ")){
            String[] classNames = className.split(" ");
            Arrays.stream(classNames).forEach(s -> {
                if (!styleClasses.contains(className)){
                    styleClasses.add(className);
                }
            });
        }else{
            styleClasses.add(className);
        }

        //listener needs to know that the class has changed
        if (ownerWindow != null){
            ownerWindow.controlClassUpdated();
        }
    }
    public void addStyleClass(String className){
        if (!styleClasses.contains(className)){
            styleClasses.add(className);

            if (ownerWindow != null){
                ownerWindow.controlClassUpdated();
            }
        }
    }
    public void removeStyleClass(String className){
        if (styleClasses.contains(className)){
            styleClasses.remove(className);

            if (ownerWindow != null){
                ownerWindow.controlClassUpdated();
            }
        }
    }

    /** Class name for styling **/
    public String getStyleClassName() {
        return String.join(" ", styleClasses);
    }
    public boolean hasStyleClass(String className){
        return styleClasses.contains(className);
    }
    public ArrayList<String> getStyleClasses(){
        return styleClasses;
    }

    public BaseControl getParent() {
        return parent;
    }
    public int indexOfChild(BaseControl child){
        return children.indexOf(child);
    }
    public int childCount(){
        return children.size();
    }
    public BaseControl getChildAt(int index){
        if (index < children.size()){
            return children.get(index);
        }else {
            return null;
        }
    }
    public BaseControl getNextSibling(){
        if (parent != null){
            int myIndex = parent.indexOfChild(this);
            return parent.getChildAt(myIndex + 1);
        }
        return null;
    }

    public void addPseudoClass(PseudoClass pClass){
        if (!currentPseudoClasses.contains(pClass.pClassName)){
            currentPseudoClasses.add(pClass.pClassName);
            if (ownerWindow != null){
                ownerWindow.controlClassUpdated();
            }
        }
    }
    public void removePseudoClass(PseudoClass pClass){
        if (currentPseudoClasses.contains(pClass.pClassName)){
            currentPseudoClasses.remove(pClass.pClassName);
            if (ownerWindow != null){
                ownerWindow.controlClassUpdated();
            }
        }
    }

    public boolean hasPseudoClass(PseudoClass pClass){
        return currentPseudoClasses.contains(pClass.pClassName);
    }
    public boolean hasPseudoClass(String pClassValue){
        return currentPseudoClasses.contains(pClassValue);
    }

    public ArrayList<BaseControl> getChildren(){
        return children;
    }
    public ArrayList<BaseControl> getAllDescendents(){
        Stack<BaseControl> controlStack = new Stack<>();
        ArrayList<BaseControl> result = new ArrayList<>();

        controlStack.push(this);

        while (!controlStack.isEmpty()){
            BaseControl control = controlStack.pop();
            control.getChildren().forEach(child -> {
                result.add(child);

                if (child.childCount() != 0){
                    controlStack.push(child);
                }
            });
        }

        return result;
    }

    public String getBaseObjectStyleType() {
        return baseObjectStyleType;
    }

    public void setStyleObjectName(String baseObjectStyleType) {
        this.baseObjectStyleType = baseObjectStyleType;
    }

    public void setStyleProperty(ECSSProperty property, Object value, ECSSUnit unit){
        boolean doTransition = transitionDefinitions.containsKey(property);

        if (doTransition){
            Object transitionVal = CSSPropertyDefaults.getPropertyDefaultValue(property);
            if (styleProperties.containsKey(property)){
                transitionVal = styleProperties.get(property).getEffectiveValue();
            }

            styleProperties.computeIfAbsent(property, p -> new StyleProperty<>(value)).setCurrentAndEffectiveValue(value, transitionVal, unit);
            ownerWindow.startControlTransition(this, property, value);
        }else{
            styleProperties.computeIfAbsent(property, p -> new StyleProperty<>(value)).setCurrentAndEffectiveValue(value, unit);
        }

        boolean hasSpecificListener = stylePropertyChangeListeners.containsKey(property);
        if (hasSpecificListener){
            stylePropertyChangeListeners.get(property).accept(this, value);
        }
    }
    /** Clears all property overrides - doesn't affect listeners. **/
    public void resetStyleProperties(){
        styleProperties.clear();
    }
    public <T extends StyleProperty<?>> T getProperty(ECSSProperty property){
        return (T) styleProperties.get(property);
    }
    public HashMap<ECSSProperty, StyleProperty<?>> getStyleProperties(){
        return styleProperties;
    }

    public void defineStylePropertyTransition(ECSSProperty property, TransitionDefinition definition){
        transitionDefinitions.put(property, definition);
    }
    public void removeTransitionDefinitions(){
        transitionDefinitions.clear();
    }
    public void clearActiveTransitions(){
        transitionInstances.clear();
    }
    public HashMap<ECSSProperty, TransitionInstance> getActiveTransitions(){
        return transitionInstances;
    }
    public HashMap<ECSSProperty, TransitionDefinition> getTransitionDefinitions(){
        return transitionDefinitions;
    }
    public TransitionDefinition getTransitionDefinitionForProperty(ECSSProperty property){
        return transitionDefinitions.get(property);
    }

    public Object getEffectiveValue(ECSSProperty property){
        boolean hasKey = styleProperties.containsKey(property);
        Object result = null;
        boolean parentTest = false;
        boolean getDefault = false;

        if (hasKey){
            StyleProperty<?> propertySource = styleProperties.get(property);
            if (propertySource.getCurrentValue() == CSSEnums.UNSET){
                //get the default value
                getDefault = true;
            }else if (propertySource.getCurrentValue() == CSSEnums.INHERIT){
                parentTest = true;
            }else{
                result = propertySource.getEffectiveValue();
            }
        }

        if (!hasKey || parentTest){
            if (parent != null || CSSPropertyDefaults.propertyIsInherited(property)){
                //is the property inheritable
                result = parent.getEffectiveValue(property);
            }else{
                //return the initial value
                getDefault = true;
            }
        }
        if (getDefault){
            result = CSSPropertyDefaults.getPropertyDefaultValue(property);
        }

        return result;
    }

    public void propagateMouseEvent(MouseEvent event){
        //check children first
        //then do my behaviours IF event isn't consumed
        //need to know my computed X and Y

        //TODO mouseevent needs to be in local window coordinates for this to work on any controls other than on windows at 0,0

        Vector2f position = getComputedPosition();
        boolean newMouseFocusState = false;
        if (event.mouseX >= position.x && event.mouseX <= position.x + computedW){
            if (event.mouseY >= position.y && event.mouseY <= position.y + computedH){
                //System.out.println(event + " / " + this.getControlID());
                children.forEach(child -> {
                    child.propagateMouseEvent(event);
                });
                newMouseFocusState = true;
            }
        }
        mouseFocused = newMouseFocusState;
        if (mouseFocused){
            ownerWindow.controlGainedMouseFocus(this);
        }else{
            removePseudoClass(PseudoClass.Hover);
            ownerWindow.controlLostMouseFocus(this);
        }
    }
    public Vector2f getComputedPosition(){
        Vector2f result = new Vector2f();
        BaseControl control = this;
        while (control != null){
            result.add(control.computedX, control.computedY);
            control = control.parent;
        }
        return result;
    }
    public boolean containsPoint(float x, float y){
        Vector2f pos = getComputedPosition();
        if (x >= pos.x && x <= pos.x + computedW){
            if (y >= pos.y && y <= pos.y + computedH){
                return true;
            }
        }
        return false;
    }
}
