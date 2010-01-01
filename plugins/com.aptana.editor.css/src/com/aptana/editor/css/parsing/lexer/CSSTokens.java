package com.aptana.editor.css.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class CSSTokens {

    public static final short UNKNOWN = -1;
    public static final short EOF = 0;
    public static final short IDENTIFIER = 1;
    public static final short COLOR = 2;
    public static final short COLON = 3;
    public static final short STRING = 4;
    public static final short LBRACKET = 5;
    public static final short CLASS = 6;
    public static final short HASH = 7;
    public static final short SEMICOLON = 8;
    public static final short RCURLY = 9;
    public static final short URL = 10;
    public static final short FUNCTION = 11;
    public static final short PLUS = 12;
    public static final short STAR = 13;
    public static final short SELECTOR = 14;
    public static final short NUMBER = 15;
    public static final short PERCENTAGE = 16;
    public static final short LENGTH = 17;
    public static final short EMS = 18;
    public static final short EXS = 19;
    public static final short ANGLE = 20;
    public static final short TIME = 21;
    public static final short FREQUENCY = 22;
    public static final short LCURLY = 23;
    public static final short RBRACKET = 24;
    public static final short COMMA = 25;
    public static final short MINUS = 26;
    public static final short PROPERTY = 27;
    public static final short PAGE = 28;
    public static final short AT_KEYWORD = 29;
    public static final short MEDIA = 30;
    public static final short CHARSET = 31;
    public static final short IMPORT = 32;
    public static final short FORWARD_SLASH = 33;
    public static final short GREATER = 34;
    public static final short RPAREN = 35;
    public static final short IMPORTANT = 36;
    public static final short EQUAL = 37;
    public static final short INCLUDES = 38;
    public static final short DASHMATCH = 39;
    public static final short COMMENT = 40;

    private static final short MAXIMUM = 40;

    @SuppressWarnings("nls")
    private static final String[] NAMES = { "EOF", "IDENTIFIER", "COLOR", "COLON", "STRING",
            "LBRACKET", "CLASS", "HASH", "SEMICOLON", "RCURLY", "URL", "FUNCTION", "PLUS", "STAR",
            "SELECTOR", "NUMBER", "PERCENTAGE", "LENGTH", "EMS", "EXS", "ANGLE", "TIME",
            "FREQUENCY", "LCURLY", "RBRACKET", "COMMA", "MINUS", "PROPERTY", "PAGE", "AT_KEYWORD",
            "MEDIA", "CHARSET", "IMPORT", "FORWARD_SLASH", "GREATER", "RPAREN", "IMPORTANT",
            "EQUAL", "INCLUDES", "DASHMATCH", "COMMENT" };
    private static final String NAME_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

    private static Map<String, Short> nameIndexMap;

    public static String getTokenName(short token) {
        init();
        if (token < 0 || token > MAXIMUM) {
            return NAME_UNKNOWN;
        }
        return NAMES[token];
    }

    public static short getToken(String tokenName) {
        init();
        Short token = nameIndexMap.get(tokenName);
        return (token == null) ? UNKNOWN : token;
    }

    private static void init() {
        if (nameIndexMap == null) {
            nameIndexMap = new HashMap<String, Short>();
            short index = 0;
            for (String name : NAMES) {
                nameIndexMap.put(name, index++);
            }
        }
    }

    private CSSTokens() {
    }
}
