package org.cstr24.hyphenengine.ui.fonts;

import org.cstr24.hyphenengine.assets.HyAssetTypeCache;

public class FontCache extends HyAssetTypeCache<HyFont> {
    public FontCache(){
        setResourceNameSupplier((res) -> res.resourceName);
    }
}
