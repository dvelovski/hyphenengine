package org.cstr24.hyphengl.ui.css;

public enum PseudoClass {
    Hover("hover"), Focus("focus"), Active("active");

    public final String declName;
    PseudoClass(String name){
        this.declName = name;
    }
}
