package org.cstr24.hyphenengine.ui.css;

import java.util.HashMap;

import static org.cstr24.hyphenengine.ui.css.PseudoClassType.*;

public enum PseudoClass {
    Hover("hover", State), Focus("focus", State), Active("active", State),
    FirstChild("first-child", Selector), OnlyChild("only-child", Selector), LastChild("last-child", Selector), NthChild("nth-child", Selector);

    public final String pClassName;
    public final PseudoClassType pClassType;

    PseudoClass(String name, PseudoClassType type){
        this.pClassName = name;
        this.pClassType = type;
    }

    public static PseudoClass getPseudoClassByName(String name){
        switch (name.toLowerCase()){
            case "hover" -> {
                return Hover;
            }
            case "focus" -> {
                return Focus;
            }
            case "active" -> {
                return Active;
            }
            case "first-child" -> {
                return FirstChild;
            }
            case "only-child" -> {
                return OnlyChild;
            }
            case "last-child" -> {
                return LastChild;
            }
            case "nth-child" -> {
                return NthChild;
            }
            default -> {
                return null;
            }
        }
    }
}
