package org.cstr24.hyphengl.ui.css;

import com.helger.css.decl.CascadingStyleSheet;
import org.cstr24.hyphengl.assets.HyResource;

public class HyCStyleSheet extends HyResource {
    public static final String RESOURCE_TYPE = "CSS";

    public CascadingStyleSheet cssSheet;

    @Override
    public void unload() {
        cssSheet = null;
    }

    public String getSheetName(){
        return "";
    }
}
