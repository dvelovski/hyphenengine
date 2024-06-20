package org.cstr24.hyphenengine.ui.windowing;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphenengine.rendering.Colour;
import org.cstr24.hyphenengine.ui.Pen;

public class Panel extends BaseControl{
    public Panel(){
        this.setStyleObjectName("panel");
    }

    @Override
    public void render(Pen p) {
        Colour backgroundColour = ((Colour) this.getStyleProperty(ECSSProperty.BACKGROUND_COLOR).getEffectiveValue());

        if (drawBackground) {
            p.setFillColour(backgroundColour);
            renderBackground(p);
        }

        //background fill and border rendering should be central internal methods otherwise LOADS of duplication
    }
}
