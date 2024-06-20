package org.cstr24.hyphenengine.ui.datatypes;

public abstract class Fill {
    public FillType type;

    public abstract void discard();


    public abstract void apply(long ctx);
}

