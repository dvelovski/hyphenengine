package org.cstr24.hyphenengine.ui.css;

import org.cstr24.hyphenengine.core.Engine;
import org.cstr24.hyphenengine.ui.windowing.BaseControl;

public class TransitionInstance {
    public BaseControl owner;
    public TransitionDefinition definition;
    public TransitionInterpolator<?> interpolator;

    public double startTime;

    public Object startValue;
    public Object targetValue;

    public TransitionInstance(BaseControl control){
        this.owner = control;
    }
    public TransitionInstance(BaseControl control, TransitionDefinition tDef){
        this(control);
        this.definition = tDef;
    }
    public void update(){
        float s0 = (float) (startTime + definition.delay);
        float s1 = (float) Engine.currentTime();
        float perc = 0f;

        if (s1 - s0 >= 0){ //still in delay state
            perc = (s1 - s0) / definition.duration;
        }

        perc = Math.max(0, Math.min(1, perc));

        float timed = definition.timingFunction.solve(perc);

        //System.out.println("perc: " + perc + " / timing: " + timed);

        owner.getStyleProperty(definition.targetProperty).setEffectiveValue(interpolator.interpolate(startValue, targetValue, timed));
    }

    public void start(){
        startTime = Engine.currentTime();
    }

    public boolean completed(){
        return Engine.currentTime() > startTime + definition.delay + definition.duration;
    }
}
