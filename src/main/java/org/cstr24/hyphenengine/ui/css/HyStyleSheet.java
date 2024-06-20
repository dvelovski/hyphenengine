package org.cstr24.hyphenengine.ui.css;

import com.helger.css.decl.CascadingStyleSheet;
import org.cstr24.hyphenengine.assets.HyAsset;

public class HyStyleSheet extends HyAsset {
    public static final String RESOURCE_TYPE = "CSS";

    public CascadingStyleSheet cssSheet;

    public HyStyleSheet(){
        this.assetType = RESOURCE_TYPE;
    }

    @Override
    public void unload() {
        cssSheet = null;
    }
}
