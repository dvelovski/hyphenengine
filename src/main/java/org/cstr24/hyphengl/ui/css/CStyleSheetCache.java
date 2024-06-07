package org.cstr24.hyphengl.ui.css;

import org.cstr24.hyphengl.assets.HyResourceCache;

public class CStyleSheetCache extends HyResourceCache<HyCStyleSheet> {
    public CStyleSheetCache(){
        setResourceNameSupplier((res) -> res.resourceName);
    }
}
