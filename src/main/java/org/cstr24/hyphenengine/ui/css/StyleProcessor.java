package org.cstr24.hyphenengine.ui.css;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.ECSSUnit;
import com.helger.css.decl.*;
import com.helger.css.decl.shorthand.CSSShortHandDescriptor;
import com.helger.css.decl.shorthand.CSSShortHandRegistry;
import com.helger.css.property.ECSSProperty;
import com.helger.css.propertyvalue.CSSSimpleValueWithUnit;
import com.helger.css.utils.CSSColorHelper;
import com.helger.css.utils.CSSNumberHelper;
import com.helger.css.utils.ECSSColor;
import org.cstr24.hyphenengine.assets.HyAssetHandle;
import org.cstr24.hyphenengine.rendering.Colour;
import org.cstr24.hyphenengine.ui.windowing.BaseControl;
import org.cstr24.hyphenengine.ui.windowing.CoordinateType;
import org.cstr24.hyphenengine.ui.windowing.HyWindow;

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
        ArrayList<HyAssetHandle<HyStyleSheet>> styleSheets = ownerWindow.getStyleSheets();

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

        //System.out.println("*** selector *** ");
        for (int mIndex = memberStartIndex; mIndex < selector.getMemberCount(); mIndex++){
            ICSSSelectorMember cMember = selector.getMemberAtIndex(mIndex);
            String mCSS = cMember.getAsCSSString();

            //System.out.println(" -> selector member: " + mCSS);
            if (Combinators.isKnownCombinator(mCSS)){
                results = Combinators.get(mCSS).applyCombinator(results);
                //System.out.println("applying combinator " + mCSS + " - " + Combinators.get(mCSS).getName());
            }else{
                String stripped = mCSS.substring(1);
                for (int i = results.size() - 1; i >= 0; i--){
                    BaseControl control = results.get(i);
                    boolean keep = false;

                    if (mCSS.startsWith(":")){
                        //System.out.println("pseudo selector: " + mCSS);
                        //how to parse if it's a 'function'?
                    }

                    if (mCSS.startsWith("#") && control.getControlID().equals(stripped)){
                        //System.out.println("control " + control + " matches ID " + mCSS);
                        keep = true;
                    }else if (mCSS.startsWith(".") && control.getStyleClassName().equals(stripped)){
                        //System.out.println("control " + control + " matches class " + mCSS);
                        keep = true;
                    }else if (mCSS.startsWith(":")){
                        String pSelector = mCSS;
                        String selectorParams = "";

                        int oParenIdx = pSelector.indexOf("(");
                        int cParenIdx = pSelector.indexOf(")");
                        if (oParenIdx > -1 && cParenIdx > -1){
                            selectorParams = mCSS.substring(oParenIdx + 1, cParenIdx);
                        }
                        if (stripped.contains("(")){
                            stripped = stripped.substring(0, stripped.indexOf("("));
                        }

                        PseudoClass pClass = PseudoClass.getPseudoClassByName(stripped);

                        if (pClass != null && pseudoClassApplicable(control, pClass, selectorParams)){
                            keep = true;
                        }
                        //System.out.println("control " + control + " matches class " + mCSS);
                    }else if (mCSS.equals(control.getBaseObjectStyleType())){
                        //System.out.println("control " + control + " matches object class " + mCSS);
                        keep = true;
                    }else if (mCSS.equals("*")){
                        //System.out.println("control " + control + " matches wildcard *");
                        keep = true;
                    }

                    if (!keep){
                        results.remove(i);
                    }
                }
            }
        }

        return results;
    }

    public boolean pseudoClassApplicable(BaseControl control, PseudoClass pClass, String functionParameters){
        if (pClass.pClassType == PseudoClassType.State){
            return control.hasPseudoClass(pClass);
        }else if (pClass.pClassType == PseudoClassType.Selector){
            switch (pClass){
                case FirstChild -> {
                    BaseControl parent = control.getParent();
                    if (parent != null){
                        return parent.indexOfChild(control) == 0;
                    }
                }
                case OnlyChild -> {
                    BaseControl parent = control.getParent();
                    return (parent == null || parent.getChildren().size() == 1);
                }
                case LastChild -> {
                    BaseControl parent = control.getParent();
                    if (parent != null){
                        return control == parent.getChildren().getLast();
                    }
                }
                case NthChild -> {
                    BaseControl parent = control.getParent();
                    if (parent != null){
                        int indexOfControl = parent.indexOfChild(control) + 1;
                        switch (functionParameters){
                            case "even" -> {
                                return indexOfControl % 2 == 0;
                            }
                            case "odd" -> {
                                return indexOfControl % 2 == 1;
                            }
                            default -> {
                                System.out.println("unsupported function parameter " + functionParameters + " for " + PseudoClass.NthChild);
                            }
                        }
                    }
                }
            }
        }
        return false;
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
                        /*CSSShortHandDescriptor aSHD = CSSShortHandRegistry.getShortHandDescriptor(ECSSProperty.getFromNameOrNull(property));

                        if (aSHD != null) {
                            List<CSSDeclaration> declarations = aSHD.getSplitIntoPieces(decl);
                            declarations.forEach(splitDeclaration -> {
                                testRuleOverride(match.styleRule, splitDeclaration, match.selector, match.specificity, ruleMatchMap);
                            });
                        }*/
                        testRuleOverride(match.styleRule, decl, match.selector, match.specificity, ruleMatchMap);
                    }else{
                        testRuleOverride(match.styleRule, decl, match.selector, match.specificity, ruleMatchMap);
                    }
                });
            });
        });

        controlApplyMap.forEach((control, map) -> {
            map.forEach((key, rMatch) -> {
                CSSDeclaration declaration = rMatch.rule.getDeclarationOfPropertyName(key);

                if (declaration != null){
                    ECSSProperty cssPropertyDef = ECSSProperty.getFromNameOrNull(declaration.getProperty());
                    setProperty(cssPropertyDef, declaration, control);
                }else{
                    //TODO set defaults
                    //System.out.println("key " + key + " is present");
                }
            });

            //for anything that's not in the map, remove it
            HashMap<ECSSProperty, StyleProperty<?>> controlProperties = control.getStyleProperties();
            ArrayList<ECSSProperty> toRemove = new ArrayList<>();
            controlProperties.keySet().forEach(key -> {
                String kName = key.getName();
                if (!map.containsKey(kName)){
                    //System.out.println("Control has property " + kName + " which is not specified in any rules.");
                    toRemove.add(key);
                }
            });
            toRemove.forEach(controlProperties::remove);
            if (toRemove.contains(ECSSProperty.TRANSITION)){
                control.removeTransitionDefinitions();
                control.clearActiveTransitions();
            }
        });
    }

    public void setProperty(ECSSProperty property, CSSDeclaration declaration, BaseControl control){
        switch (property){
            case BACKGROUND_COLOR, COLOR -> {
                handleColour(property, declaration, control);
            }
            case FONT_FAMILY -> {
                handleFontFamily(property, declaration, control);
            }
            case FONT_SIZE -> {
                handleNumericProperty(property, declaration, control);
            }
            case WIDTH, HEIGHT -> {
                handleDimensionProperty(property, declaration, control);
            }
            case TRANSITION -> {
                handleTransitionProperty(property, declaration, control);
            }
            default -> {
                handleGenericProperty(property, declaration, control);
            }
        }
    }

    public void handleGenericProperty(ECSSProperty property, CSSDeclaration declaration, BaseControl target){

        boolean shorthand = CSSShortHandRegistry.isShortHandProperty(property);
        if (shorthand){
            //System.out.println(property + " is shorthand property.");
            CSSShortHandDescriptor aSHD = CSSShortHandRegistry.getShortHandDescriptor(property);
            aSHD.getSplitIntoPieces(declaration).forEach(subDec -> {
                //System.out.println("setting sub declaration: " + subDec.getProperty());
                handleGenericProperty(ECSSProperty.getFromNameOrNull(subDec.getProperty()), subDec, target);
            });
        }else{
            String expressionVal = declaration.getExpression().getAsCSSString();
            boolean hasUnit = CSSNumberHelper.isValueWithUnit(expressionVal);

            if (hasUnit){
                CSSSimpleValueWithUnit value = CSSNumberHelper.getValueWithUnit(expressionVal);
                //we want the number and the unit
                target.setStyleProperty(property, value.getValue(), value.getUnit());
            }else{
                //set as whatever expression css is and px
                target.setStyleProperty(property, expressionVal, ECSSUnit.PX);
            }
        }
    }

    public void handleTransitionProperty(ECSSProperty property, CSSDeclaration declaration, BaseControl target){
        boolean shorthand = CSSShortHandRegistry.isShortHandProperty(property);

        //split it up - when we encounter a property declaration, this is a subsequent override
        ECSSProperty currentTargetProperty = ECSSProperty.ALL;
        ArrayList<String> declaredParameters = new ArrayList<>();
        ArrayList<ECSSProperty> detectedProperties = new ArrayList<>();

        ICommonsList<CSSExpressionMemberTermSimple> allSimpleMembers = declaration.getExpression().getAllSimpleMembers();
        for (int i = 0; i < allSimpleMembers.size(); i++){
            String memberValue = allSimpleMembers.get(i).getOptimizedValue();

            //TODO support 'all'
            //TODO remove transitions if not declared

            if (ECSSProperty.getFromNameOrNull(memberValue) != null){
                //System.out.println("encountered a property: " + memberValue);

                //does declared parameters have contents?
                //todo move to a new function
                //todo 'behaviours' - is this worth implementing - i've never used them - ?

                if (!declaredParameters.isEmpty()){
                    var result = constructTransitionDefinition(currentTargetProperty, declaredParameters);
                    target.defineStylePropertyTransition(currentTargetProperty, result);
                }

                //clear, reset state
                declaredParameters.clear();
                currentTargetProperty = ECSSProperty.getFromNameOrNull(memberValue);
                detectedProperties.add(currentTargetProperty);
            }else{
                declaredParameters.add(memberValue);
            }
        }
        if (!declaredParameters.isEmpty()){
            var result = constructTransitionDefinition(currentTargetProperty, declaredParameters);
            target.defineStylePropertyTransition(currentTargetProperty, result);
        }
        //get transition definitions, if there are any that don't match we need to:
        // - remove the definition
        // - the transitionprocessor will then clear the instance
        target.getActiveTransitions().entrySet().removeIf(entry -> !detectedProperties.contains(entry.getKey()));
    }

    public TransitionDefinition constructTransitionDefinition(ECSSProperty currentTargetProperty, ArrayList<String> declaredParameters){
        //System.out.println("*** transition definition " + currentTargetProperty + " ***");
        //assemble an object
        boolean encounteredDuration = false;
        boolean encounteredDelay = false;
        boolean encounteredEase = false;

        CSSSimpleValueWithUnit delay = null;
        CSSSimpleValueWithUnit duration = null;
        String timingFuncName = "";

        for (int j = 0; j < declaredParameters.size(); j++){
            String declParam = declaredParameters.get(j);
            //System.out.println(">parameter declared: " + declParam);

            if (CSSNumberHelper.isValueWithUnit(declParam)){
                if (!encounteredDuration){
                    //System.out.println(">>duration value potentially specified " + declParam);
                    duration = CSSNumberHelper.getValueWithUnit(declParam);
                    encounteredDuration = true;
                }else if (!encounteredDelay){
                    //System.out.println(">>delay value " + declParam);
                    delay = CSSNumberHelper.getValueWithUnit(declParam);
                    encounteredDelay = true;
                }else{
                    //System.out.println(">>extraneous declaration " + declParam);
                }
            }else{
                //System.out.println(">>may be an ease function or 'behaviour': " + declParam);
                //might be an easing function
                if (!encounteredEase){
                    if (declParam.startsWith("cubic-")){
                        //System.out.println("cubic-bezier found, need to parse parameters");
                    }else{
                        //System.out.println(declParam);
                    }
                    timingFuncName = declParam;
                }
            }
        }

        //now we've got everything we need to create a transition object
        TransitionDefinition definition = new TransitionDefinition();
        definition.duration = (float) (duration != null ? duration.getValue() : 0);
        definition.delay = (float) (delay != null ? delay.getValue() : 0);
        definition.targetProperty = currentTargetProperty;
        definition.timingFunction = TransitionTimingFunction.getOrDefault(timingFuncName);

        return definition;
    }

    public void handleColour(ECSSProperty property, CSSDeclaration declaration, BaseControl target){
        declaration.getExpression().getAllSimpleMembers().forEach(member -> {
            String memberValue = member.getOptimizedValue();

            if (ECSSColor.isDefaultColorName(memberValue)){
                CSSRGB colorValue = ECSSColor.getFromNameCaseInsensitiveOrNull(memberValue).getAsRGB();
                float colorR = Float.parseFloat(colorValue.getRed()) / 255.f;
                float colorG = Float.parseFloat(colorValue.getGreen()) / 255.f;
                float colorB = Float.parseFloat(colorValue.getBlue()) / 255.f;

                target.setStyleProperty(property, new Colour(colorR, colorG, colorB, 1.0f), null);
            }else if (CSSColorHelper.isRGBColorValue(memberValue)){

            }else if (CSSColorHelper.isHexColorValue(memberValue)){

            }
        });
    }

    public void handleFontFamily(ECSSProperty property, CSSDeclaration declaration, BaseControl target){
        //i only care about the first one
        CSSExpressionMemberTermSimple result = declaration.getExpression().getAllSimpleMembers().get(0);
        String fResult = result.getOptimizedValue();
        if (result.isStringLiteral()){
            fResult = fResult.substring(1, fResult.length() - 1);
        }

        target.setStyleProperty(property, fResult, null);
    }

    public void handleNumericProperty(ECSSProperty property, CSSDeclaration declaration, BaseControl target){
        String expressionCSS = declaration.getExpression().getAsCSSString();
        int result = 0;

        boolean hasUnit = CSSNumberHelper.isValueWithUnit(expressionCSS);

        if (hasUnit){
            CSSSimpleValueWithUnit val = CSSNumberHelper.getValueWithUnit(expressionCSS);
            target.setStyleProperty(property, val != null ? val.getValue() : 0, val.getUnit());
        }else{
            target.setStyleProperty(property, Float.parseFloat(expressionCSS), ECSSUnit.PX);
        }
    }

    public void handleDimensionProperty(ECSSProperty property, CSSDeclaration declaration, BaseControl target){
        String expressionCSS = declaration.getExpression().getAsCSSString();
        boolean hasUnit = CSSNumberHelper.isValueWithUnit(expressionCSS);
        boolean setAbsolute = false;
        boolean setAuto = false;

        float value = 0;
        ECSSUnit unitType;

        if (hasUnit){
            CSSSimpleValueWithUnit val = CSSNumberHelper.getValueWithUnit(expressionCSS);
            value = (float) val.getValue();
            unitType = val.getUnit();

            if (unitType == ECSSUnit.PX){
                setAbsolute = true;
            }else if (unitType == ECSSUnit.PERCENTAGE){
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
            unitType = ECSSUnit.PX;
        }

        if (setAbsolute){
            if (property == ECSSProperty.WIDTH){
                target.setWidthAbsolute(value);
            }else if (property == ECSSProperty.HEIGHT){
                target.setHeightAbsolute(value);
            }
        }else if (setAuto){
            if (property == ECSSProperty.WIDTH){
                target.setSizingMethodW(CoordinateType.Auto);
            }else if (property == ECSSProperty.HEIGHT){
                target.setSizingMethodH(CoordinateType.Auto);
            }
        }

        target.setStyleProperty(property, value, unitType);
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
