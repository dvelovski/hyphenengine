package org.cstr24.hyphengl.ui.fonts;

import org.cstr24.hyphengl.assets.HyResourceCache;

public class FontCache extends HyResourceCache<HyFont> {
    public FontCache(){
        setResourceNameSupplier((res) -> res.resourceName);
    }
}
