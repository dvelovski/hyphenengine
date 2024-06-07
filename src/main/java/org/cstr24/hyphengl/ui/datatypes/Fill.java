package org.cstr24.hyphengl.ui.datatypes;

public abstract class Fill {
    public FillType type;

    public abstract void discard();


    public abstract void apply(long ctx);
}

