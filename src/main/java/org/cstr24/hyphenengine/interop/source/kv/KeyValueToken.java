package org.cstr24.hyphenengine.interop.source.kv;

public class KeyValueToken {
    public String value;
    public KeyValueTokenType type;

    public KeyValueToken(String _val, KeyValueTokenType _type){
        this.value = _val;
        this.type = _type;
    }

    public String toString(){
        return type + ": " + value;
    }
}
