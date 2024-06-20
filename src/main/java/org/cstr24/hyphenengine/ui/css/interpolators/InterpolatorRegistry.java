package org.cstr24.hyphenengine.ui.css.interpolators;

import org.cstr24.hyphenengine.rendering.Colour;
import org.cstr24.hyphenengine.ui.css.TransitionInterpolator;

import java.util.HashMap;

public class InterpolatorRegistry {
    public static HashMap<Class<?>, TransitionInterpolator<?>> interpolators;

    static {
        interpolators = new HashMap<>();
        registerInterpolator(Double.class, new DoubleTransitionInterpolator());
        registerInterpolator(Colour.class, new ColourTransitionInterpolator());
    }

    public static void registerInterpolator(Class<?> clazz, TransitionInterpolator<?> interpolator){
        interpolators.put(clazz, interpolator);
    }
}
