package org.cstr24.hyphenengine.ui.css;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphenengine.ui.css.interpolators.InterpolatorRegistry;
import org.cstr24.hyphenengine.ui.windowing.BaseControl;
import org.cstr24.hyphenengine.ui.windowing.HyWindow;

import java.util.ArrayList;
import java.util.HashMap;

public class TransitionProcessor {
    private HyWindow owner;
    public ArrayList<BaseControl> controlsWithTransitions;

    public TransitionProcessor(HyWindow window){
        this.owner = window;
        this.controlsWithTransitions = new ArrayList<>();
    }

    public void addControlTransition(BaseControl control, ECSSProperty property, Object targetValue){
        if (!controlsWithTransitions.contains(control)){
            controlsWithTransitions.add(control);
        }

        TransitionDefinition def = control.getTransitionDefinitionForProperty(property);

        TransitionInstance instance = new TransitionInstance(control, def);
        Object initialValue = control.getEffectiveValue(property);

        Class<?> targetValueClass = targetValue.getClass();
        TransitionInterpolator<?> interpolator = InterpolatorRegistry.interpolators.get(targetValueClass);

        instance.startValue = initialValue;
        instance.targetValue = targetValue;
        instance.interpolator = interpolator;

        control.getActiveTransitions().put(property, instance);

        instance.start();
    }

    public void update(){
        controlsWithTransitions.forEach(control -> {
            HashMap<ECSSProperty, TransitionInstance> activeTransitions = control.getActiveTransitions();
            HashMap<ECSSProperty, TransitionDefinition> transitionDefinitions = control.getTransitionDefinitions();

            //if active contains any that aren't in definitions, remove it immediately, alternatively if the control no longer has the property
            activeTransitions.entrySet().removeIf(entry -> !transitionDefinitions.containsKey(entry.getKey()) || !control.getStyleProperties().containsKey(entry.getKey()));

            activeTransitions.values().forEach(TransitionInstance::update);

            activeTransitions.entrySet().removeIf(entry -> entry.getValue().completed());

        });
        controlsWithTransitions.removeIf(control -> control.getActiveTransitions().isEmpty());
    }
}
