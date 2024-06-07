package org.cstr24.hyphengl.ui.css;

import com.helger.css.property.CSSPropertyColor;

public class StyleProperty<T>{
    public static final String BACKGROUND_COLOR = "background-color";
    public static final String COLOR = "color";

    private T currentValue;
    private T initialValue;
    private String name = "";

    public StyleProperty(T initial){
        initialValue = initial;
        currentValue = initial;
    }

    public T getCurrentValue(){
        return currentValue;
    }
    public void setCurrentValue(Object newValue){
        currentValue = (T) newValue;
    }
}
