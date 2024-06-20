package org.cstr24.hyphenengine.rendering.surfaces;

public class TextureMetadata implements PropertyMetadata {
    public int textureType = -1; //user-defined texture type constant which a renderer will then map to a texture unit
    //when switching to, and setting textures, we will compare MaterialProperties with associated TextureMetadatas
    //we sort by order of mapped texture unit and see if new binds need to be made
    //materials should be ordered by number of as few texture switches as possible, meaning two with the same textures can avoid any texture switches

    //i.e.
    /*
        sort -> {
            compare(unit1, unit1) - same texture
            compare(unit2, unit2)
        }
     */
    public TextureTypeFilter filter = TextureTypeFilter.None; //can limit what's set in here
}
