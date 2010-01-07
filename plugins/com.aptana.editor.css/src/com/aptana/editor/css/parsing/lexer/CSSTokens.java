package com.aptana.editor.css.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class CSSTokens {

    public static final short UNKNOWN = -1;
    static public final short EOF = 0;
    static public final short IDENTIFIER = 1;
    static public final short COLOR = 2;
    static public final short COLON = 3;
    static public final short RCURLY = 4;
    static public final short SEMICOLON = 5;
    static public final short LBRACKET = 6;
    static public final short CLASS = 7;
    static public final short HASH = 8;
    static public final short STRING = 9;
    static public final short STAR = 10;
    static public final short SELECTOR = 11;
    static public final short FUNCTION = 12;
    static public final short URL = 13;
    static public final short LCURLY = 14;
    static public final short COMMA = 15;
    static public final short NUMBER = 16;
    static public final short PERCENTAGE = 17;
    static public final short LENGTH = 18;
    static public final short EMS = 19;
    static public final short EXS = 20;
    static public final short ANGLE = 21;
    static public final short TIME = 22;
    static public final short FREQUENCY = 23;
    static public final short PAGE = 24;
    static public final short AT_KEYWORD = 25;
    static public final short CHARSET = 26;
    static public final short MEDIA = 27;
    static public final short RBRACKET = 28;
    static public final short IMPORT = 29;
    static public final short PROPERTY = 30;
    static public final short FORWARD_SLASH = 31;
    static public final short PLUS = 32;
    static public final short MINUS = 33;
    static public final short RPAREN = 34;
    static public final short IMPORTANT = 35;
    static public final short EQUAL = 36;
    static public final short INCLUDES = 37;
    static public final short DASHMATCH = 38;
    public static final short COMMENT = 39;

    private static final short MAXIMUM = 39;

    @SuppressWarnings("nls")
    private static final String[] NAMES = { "EOF", "IDENTIFIER", "COLOR", "COLON", "RCURLY",
            "SEMICOLON", "LBRACKET", "CLASS", "HASH", "STRING", "STAR", "SELECTOR", "FUNCTION",
            "URL", "LCURLY", "COMMA", "NUMBER", "PERCENTAGE", "LENGTH", "EMS", "EXS", "ANGLE",
            "TIME", "FREQUENCY", "PAGE", "AT_KEYWORD", "CHARSET", "MEDIA", "RBRACKET", "IMPORT",
            "PROPERTY", "FORWARD_SLASH", "PLUS", "MINUS", "RPAREN", "IMPORTANT", "EQUAL",
            "INCLUDES", "DASHMATCH", "COMMENT" };
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
