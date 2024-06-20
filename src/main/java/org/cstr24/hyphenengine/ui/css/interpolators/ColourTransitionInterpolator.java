package org.cstr24.hyphenengine.ui.css.interpolators;

import org.cstr24.hyphenengine.rendering.Colour;
import org.cstr24.hyphenengine.ui.css.TransitionInterpolator;

public class ColourTransitionInterpolator extends TransitionInterpolator<Colour> {
    @Override
    public Colour interpolate(Object start, Object end, double time) {
        Colour sColour = ((Colour) start);
        Colour eColour = ((Colour) end);

        float r = Math.abs(interpolateFloat(sColour.r, eColour.r, time));
        float g = Math.abs(interpolateFloat(sColour.g, eColour.g, time));
        float b = Math.abs(interpolateFloat(sColour.b, eColour.b, time));
        float a = Math.abs(interpolateFloat(sColour.a, eColour.a, time));

        return new Colour(r, g, b, a);
    }
}
