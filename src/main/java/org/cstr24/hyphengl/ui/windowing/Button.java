package org.cstr24.hyphengl.ui.windowing;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphengl.rendering.Colour;
import org.cstr24.hyphengl.ui.Pen;

public class Button extends BaseControl {
    public String caption = "";

    public Button(){
        setStyleObjectName("button");
    }

    @Override
    public void render(Pen p) {
        super.render(p);

        String fontName = (String) this.getStyleProperty(ECSSProperty.FONT_FAMILY).getCurrentValue();
        Integer fontSize = ((Integer) this.getStyleProperty(ECSSProperty.FONT_SIZE).getCurrentValue());
        Colour fontColour = ((Colour) this.getStyleProperty(ECSSProperty.COLOR).getCurrentValue());

        String textAlign = ((String) this.getStyleProperty(ECSSProperty.TEXT_ALIGN).getCurrentValue());

        p.setFillColour(((Colour) this.getStyleProperty(ECSSProperty.BACKGROUND_COLOR).getCurrentValue()));
        p.fillRectangle(this.computedX, this.computedY, this.computedW, this.computedH);

        p.setFont(fontName, fontSize);
        p.setStrokeColour(fontColour);

        float[] bounds = {0, 0, 0, 0};
        p.getTextBounds(caption, bounds);
        float textW = bounds[2];
        float textH = bounds[3];
        float textY = (computedY + (textH / 2));

        switch (textAlign){
            case "right" -> {
                p.drawString(caption, computedX + computedW - textW, textY);
            }
            case "center" -> {
                p.drawString(caption, computedX + (computedW / 2) - (textW / 2), textY);
            }
            default -> {
                p.drawString(caption, computedX, textY);
            }
        }
    }
}
