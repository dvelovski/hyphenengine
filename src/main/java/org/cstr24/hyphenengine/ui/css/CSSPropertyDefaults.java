package org.cstr24.hyphenengine.ui.css;

import com.helger.css.property.ECSSProperty;
import org.cstr24.hyphenengine.rendering.Colour;

import java.util.HashMap;

public class CSSPropertyDefaults {
    public static final HashMap<ECSSProperty, CSSPropertyDefinition> propertyDefaults = new HashMap<>();
    static {
        propertyDefaults.put(ECSSProperty.BACKFACE_VISIBILITY, new CSSPropertyDefinition(CSSEnums.VISIBLE, false));

        propertyDefaults.put(ECSSProperty.BACKGROUND_ATTACHMENT, new CSSPropertyDefinition(CSSEnums.SCROLL, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_BLEND_MODE, new CSSPropertyDefinition(CSSEnums.NORMAL, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_CLIP, new CSSPropertyDefinition(CSSEnums.BORDER_BOX, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_COLOR, new CSSPropertyDefinition(new Colour(0, 0, 0, 0), false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_IMAGE, new CSSPropertyDefinition(CSSEnums.NONE, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_ORIGIN, new CSSPropertyDefinition(CSSEnums.BORDER_BOX, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_REPEAT, new CSSPropertyDefinition(CSSEnums.REPEAT, false));
        propertyDefaults.put(ECSSProperty.BACKGROUND_SIZE, new CSSPropertyDefinition(CSSEnums.AUTO, false));

        propertyDefaults.put(ECSSProperty.BORDER_COLOR, new CSSPropertyDefinition(new Colour(0, 0, 0, 1), false));
        propertyDefaults.put(ECSSProperty.BORDER_IMAGE_OUTSET, new CSSPropertyDefinition(0D, false));
        propertyDefaults.put(ECSSProperty.BORDER_IMAGE_REPEAT, new CSSPropertyDefinition(CSSEnums.STRETCH, false));
        propertyDefaults.put(ECSSProperty.BORDER_IMAGE_SLICE, new CSSPropertyDefinition(100D, false));
        propertyDefaults.put(ECSSProperty.BORDER_IMAGE_SOURCE, new CSSPropertyDefinition(CSSEnums.NONE, false));
        propertyDefaults.put(ECSSProperty.BORDER_IMAGE_WIDTH, new CSSPropertyDefinition(1D, false));
        propertyDefaults.put(ECSSProperty.BORDER_TOP_WIDTH, new CSSPropertyDefinition(2D, false));
        propertyDefaults.put(ECSSProperty.BORDER_RIGHT_WIDTH, new CSSPropertyDefinition(2D, false));
        propertyDefaults.put(ECSSProperty.BORDER_BOTTOM_WIDTH, new CSSPropertyDefinition(2D, false));
        propertyDefaults.put(ECSSProperty.BORDER_LEFT_WIDTH, new CSSPropertyDefinition(2D, false));

        propertyDefaults.put(ECSSProperty.BORDER_TOP_LEFT_RADIUS, new CSSPropertyDefinition(0D, false));
        propertyDefaults.put(ECSSProperty.BORDER_TOP_RIGHT_RADIUS, new CSSPropertyDefinition(0D, false));
        propertyDefaults.put(ECSSProperty.BORDER_BOTTOM_RIGHT_RADIUS, new CSSPropertyDefinition(0D, false));
        propertyDefaults.put(ECSSProperty.BORDER_BOTTOM_LEFT_RADIUS, new CSSPropertyDefinition(0D, false));

        propertyDefaults.put(ECSSProperty.COLOR, new CSSPropertyDefinition(new Colour(0, 0, 0, 1), false));

        propertyDefaults.put(ECSSProperty.CURSOR, new CSSPropertyDefinition(CursorEnums.Auto, true));

        propertyDefaults.put(ECSSProperty.FILTER, new CSSPropertyDefinition(CSSEnums.NONE, false));

        propertyDefaults.put(ECSSProperty.FLEX_BASIS, new CSSPropertyDefinition(CSSEnums.AUTO, false));
        propertyDefaults.put(ECSSProperty.FLEX_DIRECTION, new CSSPropertyDefinition(FlexEnums.Row, false));
        propertyDefaults.put(ECSSProperty.FLEX_GROW, new CSSPropertyDefinition(0D, false));
        propertyDefaults.put(ECSSProperty.FLEX_SHRINK, new CSSPropertyDefinition(1D, false));
        propertyDefaults.put(ECSSProperty.FLEX_WRAP, new CSSPropertyDefinition(FlexEnums.NoWrap, false));

        propertyDefaults.put(ECSSProperty.FONT, new CSSPropertyDefinition("Open Sans", false));

        propertyDefaults.put(ECSSProperty.HANGING_PUNCTUATION, new CSSPropertyDefinition(CSSEnums.NONE, false));

        propertyDefaults.put(ECSSProperty.HEIGHT, new CSSPropertyDefinition(CSSEnums.AUTO, false));
        propertyDefaults.put(ECSSProperty.HYPHENS, new CSSPropertyDefinition(CSSEnums.AUTO, true));
    }


    public static CSSPropertyDefinition getPropertyDefinition(ECSSProperty property){
        return propertyDefaults.get(property);
    }
    public static Object getPropertyDefaultValue(ECSSProperty property){
        CSSPropertyDefinition def = propertyDefaults.get(property);
        if (def != null) {
            return def.defaultValue;
        } else {
            return CSSEnums.NONE;
        }
    }
    public static boolean propertyIsInherited(ECSSProperty property){
        return propertyDefaults.get(property).inherited;
    }
}
