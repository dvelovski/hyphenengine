package org.cstr24.hyphenengine.ui.css;

import com.helger.css.ECSSUnit;

public class StyleProperty<T>{
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String COLOR = "color";

    /**
     * The value that this property has been set to.
     * Note it may be animating towards this value. For the value to use for rendering, use 'effective value'.
     */
    private T currentValue;

    /**
     * Not sure yet.
     */
    private T initialValue;

    /**
     * The current 'effective' value.
     */
    private T effectiveValue;

    private String name = "";
    private ECSSUnit currentUnit;

    public StyleProperty(T initial){
        initialValue = initial;
        currentValue = initial;
    }

    public T getCurrentValue(){
        return currentValue;
    }
    public void setCurrentValue(Object newValue){
        setCurrentValue(newValue, ECSSUnit.PX);
    }
    public void setCurrentValue(Object newValue, ECSSUnit unit){
        currentValue = (T) newValue;
        currentUnit = unit;
    }
    public void setCurrentAndEffectiveValue(Object newValue, ECSSUnit unit){
        setCurrentValue(newValue, unit);
        setEffectiveValue(newValue);
    }
    public void setCurrentAndEffectiveValue(Object newValue, Object effectiveValue, ECSSUnit unit){
        setCurrentAndEffectiveValue(newValue, unit);
        setEffectiveValue(effectiveValue);
    }
    public T getEffectiveValue(){
        return effectiveValue;
    }
    public void setEffectiveValue(Object value){
        effectiveValue = (T) value;
    }

    public void setUnit(ECSSUnit unit){
        currentUnit = unit;
    }
    public ECSSUnit getUnit(){
        return currentUnit;
    }
}
