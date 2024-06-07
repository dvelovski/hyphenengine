package org.cstr24.hyphengl.ui.windowing;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphengl.rendering.Colour;
import org.cstr24.hyphengl.ui.Pen;

public class Panel extends BaseControl{
    public Panel(){
        this.setStyleObjectName("panel");
    }

    @Override
    public void render(Pen p) {
        Colour backgroundColour = ((Colour) this.getStyleProperty(ECSSProperty.BACKGROUND_COLOR).getCurrentValue());

        p.setFillColour(backgroundColour);
        p.fillRectangle(this.computedX, this.computedY, this.computedW, this.computedH);

        /*p.setFont("Open Sans", ((Integer) this.getStyleProperty(ECSSProperty.FONT_SIZE).getCurrentValue()));
        p.setStrokeColour(((Colour) this.getStyleProperty(ECSSProperty.COLOR).getCurrentValue()));
        p.drawString(getOwnerWindow().caption, computedX, computedY);*/
    }
}
