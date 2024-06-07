package org.cstr24.hyphengl.ui.css;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;
import org.cstr24.hyphengl.ui.windowing.BaseControl;

import java.util.List;

public class RuleMatchCollection {
    public int[] specificity;
    public CSSStyleRule styleRule;
    public CSSSelector selector;
    List<BaseControl> matchingControls;

    public RuleMatchCollection(CSSStyleRule rule, CSSSelector sel, int[] spec, List<BaseControl> matchList){
        this.styleRule = rule;
        this.selector = sel;
        this.specificity = spec;
        this.matchingControls = matchList;
    }
    public void setMatchingControls(List<BaseControl> matchList){
        matchingControls = matchList;
    }
}
