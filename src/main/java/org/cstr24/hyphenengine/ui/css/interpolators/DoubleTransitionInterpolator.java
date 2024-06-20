package org.cstr24.hyphenengine.ui.css.interpolators;

import org.cstr24.hyphenengine.ui.css.TransitionInterpolator;
import org.cstr24.hyphenengine.ui.css.TransitionTimingFunction;

public class DoubleTransitionInterpolator extends TransitionInterpolator<Double> {

    @Override
    public Double interpolate(Object start, Object end, double time) {
        return Math.abs(interpolateDouble((Double) start, (Double) end, time));
    }
}
