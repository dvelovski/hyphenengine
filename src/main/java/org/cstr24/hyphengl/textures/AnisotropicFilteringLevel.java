package org.cstr24.hyphengl.textures;

public enum AnisotropicFilteringLevel {
    L1(1.0f), L2(2.0f), L4(4.0f), L8(8.0f), L16(16.0f);
    public final float value;
    AnisotropicFilteringLevel(float _l){
        this.value = _l;
    }
}
