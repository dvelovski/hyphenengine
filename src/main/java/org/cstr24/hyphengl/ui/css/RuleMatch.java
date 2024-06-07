package org.cstr24.hyphengl.ui.css;

import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSStyleRule;

public class RuleMatch {
    public CSSStyleRule rule;
    public CSSSelector selector;
    public int[] specificity = {0, 0, 0};

    public RuleMatch(CSSStyleRule sRule, CSSSelector sSel, int[] sSpec){
        this.rule = sRule;
        this.selector = sSel;
        this.specificity = sSpec;
    }
}
