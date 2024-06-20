package org.cstr24.hyphenengine.rendering.state;

import java.util.HashMap;
import java.util.stream.IntStream;

public class ShaderUniformState {
    public HashMap<String, Object> values;

    public HashMap<Integer, Integer> textureUnitBindings;
    private int textureUnits;
    //the idea is we don't want to call xxUniform1xx etc... if the values in the map are the same

    {
        values = new HashMap<>();
        textureUnitBindings = new HashMap<>();
    }

    public void updateState(String _uName, Object _newValue){
        values.put(_uName, _newValue);
    }

    public void setTextureUnitCount(int _count){
        IntStream.range(0, _count).forEach(i -> textureUnitBindings.put(i, 0));
    }

    public void updateTextureUniformBinding(int _unit, int _textureRef){
        textureUnitBindings.put(_unit, _textureRef);
    }

    public boolean checkTextureUnitBinding(int _unit, int _texture){
        return textureUnitBindings.get(_unit) == _texture;
    }

    public void reset(){
        values.clear();
        textureUnitBindings.keySet().forEach(key -> {
            textureUnitBindings.put(key, 0);
        });
    }
}
