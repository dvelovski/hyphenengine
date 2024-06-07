package org.cstr24.hyphengl.ui.windowing;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphengl.rendering.Colour;
import org.cstr24.hyphengl.ui.Pen;

public class TitleBar extends Panel{
    public TitleBar() {
        setStyleObjectName("titleBar");
    }

    @Override
    public void render(Pen p) {
        super.render(p);
        //TODO current values probably should NOT be a value class but a wrapper. that way they can store the type of unit they represent (if needed).

        String fontName = (String) this.getStyleProperty(ECSSProperty.FONT_FAMILY).getCurrentValue();
        Integer fontSize = ((Integer) this.getStyleProperty(ECSSProperty.FONT_SIZE).getCurrentValue());
        Colour fontColour = ((Colour) this.getStyleProperty(ECSSProperty.COLOR).getCurrentValue());

        p.setFont(fontName, fontSize);
        p.setStrokeColour(fontColour);

        p.drawString(getOwnerWindow().caption, computedX, computedY);
    }
}
