package org.cstr24.hyphengl.ui.css;

import com.helger.css.ECSSUnit;
import com.helger.css.decl.*;
import com.helger.css.decl.shorthand.CSSShortHandDescriptor;
import com.helger.css.decl.shorthand.CSSShortHandRegistry;
import com.helger.css.property.ECSSProperty;
import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import com.helger.css.utils.CSSColorHelper;
import com.helger.css.utils.CSSNumberHelper;
import com.helger.css.utils.ECSSColor;
import org.cstr24.hyphengl.assets.HyResHandle;
import org.cstr24.hyphengl.rendering.Colour;
import org.cstr24.hyphengl.ui.windowing.BaseControl;
import org.cstr24.hyphengl.ui.windowing.CoordinateType;
import org.cstr24.hyphengl.ui.windowing.HyWindow;

import java.util.*;

public class StyleProcessor {
    public static final int SPECIFICITY_INDEX_ID = 0;
    public static final int SPECIFICITY_INDEX_CLASS = 1;
    public static final int SPECIFICITY_INDEX_OBJECT = 2;

    private HyWindow ownerWindow;

    public StyleProcessor(HyWindow owner){
        ownerWindow = owner;
    }

    public void evaluateStyleSheets(){
        BaseControl root = ownerWindow.getRootPanel();
        ArrayList<HyResHandle<HyCStyleSheet>> styleSheets = ownerWindow.getStyleSheets();

        for (int i = 0; i < styleSheets.size(); i++){
            CascadingStyleSheet cssSheet = styleSheets.get(i).get().cssSheet;
            processSheet(cssSheet, root);

        }
    }
    public int[] calculateSpecificity(CSSSelector selector){
        int[] specificityResult = {0, 0, 0};

        int selectorMemberCount = selector.getMemberCount();
        for (int mIndex = 0; mIndex < selectorMemberCount; mIndex++){
            ICSSSelectorMember cMember = selector.getMemberAtIndex(mIndex);
            String mCSS = cMember.getAsCSSString();
            //System.out.println("Selector member: " + mCSS);

            if (mCSS.startsWith("#")){ //ID
                specificityResult[SPECIFICITY_INDEX_ID]++;
            }else if (mCSS.startsWith(".") || mCSS.startsWith(":")){ //class and pseudo-class
                specificityResult[SPECIFICITY_INDEX_CLASS]++;
            }else if (!Combinators.isKnownCombinator(mCSS)){
                //need to ensure it's NOT a combinator (including blank considering that's the descendent level)
                //if it's not either one of these things, we can include it on the object specificity level
                specificityResult[SPECIFICITY_INDEX_OBJECT]++;
            }
        }

        return specificityResult;
    }

    public List<BaseControl> evalSelector2(CSSSelector selector, int memberStartIndex, ArrayList<BaseControl> controlSet){
        List<BaseControl> results = new ArrayList<>(controlSet);

        System.out.println("*** selector *** ");
        for (int mIndex = memberStartIndex; mIndex < selector.getMemberCount(); mIndex++){
            ICSSSelectorMember cMember = selector.getMemberAtIndex(mIndex);
            String mCSS = cMember.getAsCSSString();

            System.out.println(" -> selector member: " + mCSS);
            if (Combinators.isKnownCombinator(mCSS)){
                results = Combinators.get(mCSS).applyCombinator(results);
                System.out.println("applying combinator " + mCSS + " - " + Combinators.get(mCSS).getName());
            }else{
                String stripped = mCSS.substring(1);
                for (int i = results.size() - 1; i >= 0; i--){
                    BaseControl control = results.get(i);
                    boolean keep = false;

                    if (mCSS.startsWith("#") && control.getControlID().equals(stripped)){
                        System.out.println("control " + control + " matches ID " + mCSS);
                        keep = true;
                    }else if (mCSS.startsWith(".") && control.getStyleClassName().equals(stripped)){
                        System.out.println("control " + control + " matches class " + mCSS);
                        keep = true;
                    }else if (mCSS.startsWith(":") && control.hasPseudoClass(stripped)){
                        System.out.println("control " + control + " matches class " + mCSS);
                        keep = true;
                    }else if (mCSS.equals(control.getBaseObjectStyleType())){
                        System.out.println("control " + control + " matches object class " + mCSS);
                        keep = true;
                    }else if (mCSS.equals("*")){
                        System.out.println("control " + control + " matches wildcard *");
                        keep = true;
                    }

                    if (!keep){
                        results.remove(i);
                    }
                }
            }
        }
        System.out.println(" ");

        return results;
    }

    public void testRuleOverride(CSSStyleRule rule, CSSDeclaration declaration, CSSSelector selector, int[] specificity, HashMap<String, RuleMatch> ruleMatchMap){
        String declarationProperty = declaration.getProperty();
        if (ruleMatchMap.containsKey(declarationProperty)){
            RuleMatch rMatchA = ruleMatchMap.get(declarationProperty);
            RuleMatch rMatchB = new RuleMatch(rule, selector, specificity);

            ruleMatchMap.put(declarationProperty, ruleOverrides(rMatchA, rMatchB));
        }else{
            ruleMatchMap.put(declaration.getProperty(), new RuleMatch(rule, selector, specificity));
        }
    }

    public void processSheet(CascadingStyleSheet sheet, BaseControl rootControl){
        int numStyleRules = sheet.getStyleRuleCount();
        ArrayList<BaseControl> controls = rootControl.getAllDescendents();
        controls.addFirst(rootControl);

        ArrayList<RuleMatchCollection> matchList = new ArrayList<>();

        for (int rIdx = 0; rIdx < numStyleRules; rIdx++){
            CSSStyleRule rule = sheet.getStyleRuleAtIndex(rIdx);

            int selectorCount = rule != null ? rule.getSelectorCount() : 0;
            for (int sIdx = 0; sIdx < selectorCount; sIdx++){
                CSSSelector selector = rule.getSelectorAtIndex(sIdx);
                if (selector != null){
                    List<BaseControl> results = evalSelector2(selector, 0, controls);
                    int[] specificity = calculateSpecificity(selector);

                    RuleMatchCollection matchData = new RuleMatchCollection(rule, selector, specificity, results);
                    matchList.add(matchData);
                    //System.out.println(selector.getAsCSSString() + " - specificity: " + Arrays.toString(specificity));
                }
            }
        }

        applyStyles(matchList);
    }

    public void applyStyles(ArrayList<RuleMatchCollection> matchData){
        //List<BaseControl> allControls = matchData.stream().flatMap(match -> match.matchingControls.stream()).toList();
        HashMap<BaseControl, HashMap<String, RuleMatch>> controlApplyMap = new HashMap<>();

        matchData.forEach(match -> {
            var ruleDeclarations = match.styleRule.getAllDeclarations();
            match.matchingControls.forEach(control -> {
                HashMap<String, RuleMatch> ruleMatchMap = controlApplyMap.computeIfAbsent(control, baseControl -> new HashMap<>());

                ruleDeclarations.forEach(decl -> {
                    String property = decl.getProperty();

                    boolean isShorthand = CSSShortHandRegistry.isShortHandProperty(ECSSProperty.getFromNameOrNull(property));
                    if (isShorthand){
                        CSSShortHandDescriptor aSHD = CSSShortHandRegistry.getShortHandDescriptor(ECSSProperty.getFromNameOrNull(property));
                        List<CSSDeclaration> declarations = aSHD.getSplitIntoPieces(decl);

                        declarations.forEach(splitDeclaration -> {
                            testRuleOverride(match.styleRule, splitDeclaration, match.selector, match.specificity, ruleMatchMap);
                        });
                    }else{
                        testRuleOverride(match.styleRule, decl, match.selector, match.specificity, ruleMatchMap);
                    }
                });
            });
        });

        //System.out.println(controlApplyMap);

        controlApplyMap.forEach((control, map) -> {
            //System.out.println(control);
            map.forEach((key, rMatch) -> {
                CSSDeclaration declaration = rMatch.rule.getDeclarationOfPropertyName(key);

                if (declaration != null){
                    ECSSProperty cssPropertyDef = ECSSProperty.getFromNameOrNull(declaration.getProperty());
                    setProperty(cssPropertyDef, declaration.getExpression(), control);
                }else{
                    //TODO set defaults
                    //CSSPropertyWithDefaultValue property = rMatch.rule.getpr;
                }
            });
        });
    }

    public void setProperty(ECSSProperty property, CSSExpression expression, BaseControl control){
        switch (property){
            case BACKGROUND_COLOR, COLOR -> {
                handleColour(property, expression, control);
            }
            case FONT_FAMILY -> {
                handleFontFamily(property, expression, control);
            }
            case FONT_SIZE -> {
                handleNumericProperty(property, expression, control);
            }
            case WIDTH, HEIGHT -> {
                handleDimensionProperty(property, expression, control);
            }
            default -> {
                control.setStyleProperty(property, expression.getAsCSSString());
            }
        }
    }

    public void handleColour(ECSSProperty property, CSSExpression expression, BaseControl target){
        expression.getAllSimpleMembers().forEach(member -> {
            String memberValue = member.getOptimizedValue();

            if (ECSSColor.isDefaultColorName(memberValue)){
                CSSRGB colorValue = ECSSColor.getFromNameCaseInsensitiveOrNull(memberValue).getAsRGB();
                float colorR = Float.parseFloat(colorValue.getRed()) / 255.f;
                float colorG = Float.parseFloat(colorValue.getGreen()) / 255.f;
                float colorB = Float.parseFloat(colorValue.getBlue()) / 255.f;

                target.setStyleProperty(property, new Colour(colorR, colorG, colorB, 1.0f));
            }else if (CSSColorHelper.isRGBColorValue(memberValue)){

            }else if (CSSColorHelper.isHexColorValue(memberValue)){

            }
        });
    }

    public void handleFontFamily(ECSSProperty property, CSSExpression expression, BaseControl target){
        //i only care about the first one
        CSSExpressionMemberTermSimple result = expression.getAllSimpleMembers().get(0);
        String fResult = result.getOptimizedValue();
        if (result.isStringLiteral()){
            fResult = fResult.substring(1, fResult.length() - 1);
        }

        target.setStyleProperty(property, fResult);
    }

    public void handleNumericProperty(ECSSProperty property, CSSExpression expression, BaseControl target){
        String expressionCSS = expression.getAsCSSString();
        int result = 0;

        boolean hasUnit = CSSNumberHelper.isValueWithUnit(expressionCSS);

        if (hasUnit){
            CSSSimpleValueWithUnit val = CSSNumberHelper.getValueWithUnit(expressionCSS);
            result = val.getAsIntValue();
        }else{
            result = Integer.parseInt(expressionCSS);
        }
        target.setStyleProperty(property, result);
    }

    public void handleDimensionProperty(ECSSProperty property, CSSExpression expression, BaseControl target){
        String expressionCSS = expression.getAsCSSString();
        boolean hasUnit = CSSNumberHelper.isValueWithUnit(expressionCSS);
        boolean setAbsolute = false;
        boolean setAuto = false;

        int value = 0;

        if (hasUnit){
            CSSSimpleValueWithUnit val = CSSNumberHelper.getValueWithUnit(expressionCSS);
            value = val.getAsIntValue();

            if (val.getUnit() == ECSSUnit.PX){
                setAbsolute = true;
            }else if (val.getUnit() == ECSSUnit.PERCENTAGE){
                if (property == ECSSProperty.WIDTH) {
                    target.setWidthPercentage(value);
                }else if (property == ECSSProperty.HEIGHT){
                    target.setHeightPercentage(value);
                }
            }
        }else{
            if (expressionCSS.equals("auto")){
                setAuto = true;
            }else{
                value = Integer.parseInt(expressionCSS);
            }
        }

        if (setAbsolute){
            if (property == ECSSProperty.WIDTH){
                target.setWidthAbsolute(value);
            }else if (property == ECSSProperty.HEIGHT){
                target.setHeightAbsolute(value);
            }
        }else{
            if (property == ECSSProperty.WIDTH){
                target.setSizingMethodW(CoordinateType.Auto);
            }else if (property == ECSSProperty.HEIGHT){
                target.setSizingMethodH(CoordinateType.Auto);
            }
        }

        target.setStyleProperty(property, value);
    }

    public void handleWidth(String key, CSSExpression expression, BaseControl target){
        expression.getAllSimpleMembers().forEach(member -> {
            String memberValue = member.getOptimizedValue();

            if (CSSNumberHelper.isNumberValue(memberValue)){
                target.setWidthAbsolute(Float.parseFloat(memberValue));
            }else if (CSSNumberHelper.isValueWithUnit(memberValue)){
                CSSSimpleValueWithUnit value = CSSNumberHelper.getValueWithUnit(memberValue);
                switch (value.getUnit()){
                    case PERCENTAGE -> {
                        target.setWidthPercentage(Float.parseFloat(memberValue));
                    }
                    case PX -> {
                        target.setWidthAbsolute(Float.parseFloat(memberValue));
                    }
                }
            }else if (memberValue.equals("auto")){
                target.setSizingMethodW(CoordinateType.Auto);
            }
        });
    }
    public void handleDisplayProperty(String key, CSSExpression expression, BaseControl target){
        String expressionValue = expression.getAllSimpleMembers().get(0).getOptimizedValue();
        switch (expressionValue){
            case "none" -> {
                target.visible = false;
            }
            default -> {
                target.visible = true;
            }
        }
    }

    /**
     * Tests whether CSS rule 'a' overrides CSS rule 'b'.
     * @param a
     * @param b
     * @return The rule which wins the override test.
     */
    public RuleMatch ruleOverrides(RuleMatch a, RuleMatch b){
        if (a.specificity[SPECIFICITY_INDEX_ID] > b.specificity[SPECIFICITY_INDEX_ID]){
            return a;
        }else if (b.specificity[SPECIFICITY_INDEX_ID] > a.specificity[SPECIFICITY_INDEX_ID]){
            return b;
        }else{
            if (a.specificity[SPECIFICITY_INDEX_CLASS] > b.specificity[SPECIFICITY_INDEX_CLASS]){
                return a;
            }else if (b.specificity[SPECIFICITY_INDEX_CLASS] > a.specificity[SPECIFICITY_INDEX_CLASS]){
                return b;
            }else{
                if (a.specificity[SPECIFICITY_INDEX_OBJECT] > b.specificity[SPECIFICITY_INDEX_OBJECT]){
                    return a;
                }else if (b.specificity[SPECIFICITY_INDEX_OBJECT] > a.specificity[SPECIFICITY_INDEX_OBJECT]){
                    return b;
                }else{
                    int aLineNo = a.rule.getSourceLocation() != null ? a.rule.getSourceLocation().getFirstTokenBeginLineNumber() : 0;
                    int bLineNo = b.rule.getSourceLocation() != null ? b.rule.getSourceLocation().getFirstTokenBeginLineNumber() : 0;
                    if (aLineNo > bLineNo){
                        return a;
                    }
                }
            }
        }
        return b;
    }
}
