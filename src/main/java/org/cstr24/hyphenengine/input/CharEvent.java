package org.cstr24.hyphenengine.input;

public class CharEvent extends InputEvent<CharEvent> {
    public String charPressed = "";

    public CharEvent(int _codePoint){
        this(String.valueOf(Character.toChars(_codePoint)));
    }
    public CharEvent(String _input){
        this.source = Source.Char;
        charPressed = _input;
    }

    @Override
    public void innerReset() {
        charPressed = "";
    }
}
