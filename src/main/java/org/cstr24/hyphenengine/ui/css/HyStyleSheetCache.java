package org.cstr24.hyphenengine.ui.css;

import org.cstr24.hyphenengine.assets.HyAssetTypeCache;

public class HyStyleSheetCache extends HyAssetTypeCache<HyStyleSheet> {
    public HyStyleSheetCache(){
        setResourceNameSupplier((res) -> res.resourceName);
    }
}
