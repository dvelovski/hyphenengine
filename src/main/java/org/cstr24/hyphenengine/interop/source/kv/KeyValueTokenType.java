package org.cstr24.hyphenengine.interop.source.kv;

import java.util.regex.Pattern;

public enum KeyValueTokenType {
    Comment("//.*"),
    NewLine("\\r\\n"),
    Whitespace("\\s"),
    LeftBrace("\\{"),
    RightBrace("\\}"),
    Number("[\\.|-]{0,2}\\d+\\.?\\d*"),
    AlternativeValue("\\\"([^\\\"]*)\\\""),
    //Number("\\d+\\.?\\d*"),
    //TODO handle leading `-` sign and leading `.` - I would like to do it all in one regex please
    Value("\\\"(\\\\.|[^\\\"])*\\\""),
    DXDirective("<dx\\d{2}"),
    BSPDirective("%(\\S)+"),
    //PLEASE let this be all the fucking edge cases
    ValueNoQuote("(\\S)+");

    public final String patternString;
    public final Pattern regexPattern;

    KeyValueTokenType(String _pat){
        this.patternString = _pat;
        this.regexPattern = Pattern.compile(_pat);
    }
}
