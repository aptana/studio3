package com.aptana.editor.css.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class CSSTokens {

    public static final short UNKNOWN = -1;
    public static final short EOF = 0;
    public static final short IDENTIFIER = 1;
    public static final short COLOR = 2;
    public static final short COLON = 3;
    public static final short RCURLY = 4;
    public static final short SEMICOLON = 5;
    public static final short LBRACKET = 6;
    public static final short CLASS = 7;
    public static final short HASH = 8;
    public static final short STRING = 9;
    public static final short STAR = 10;
    public static final short SELECTOR = 11;
    public static final short FUNCTION = 12;
    public static final short URL = 13;
    public static final short LCURLY = 14;
    public static final short COMMA = 15;
    public static final short NUMBER = 16;
    public static final short PERCENTAGE = 17;
    public static final short LENGTH = 18;
    public static final short EMS = 19;
    public static final short EXS = 20;
    public static final short ANGLE = 21;
    public static final short TIME = 22;
    public static final short FREQUENCY = 23;
    public static final short PAGE = 24;
    public static final short AT_KEYWORD = 25;
    public static final short CHARSET = 26;
    public static final short MEDIA = 27;
    public static final short RBRACKET = 28;
    public static final short IMPORT = 29;
    public static final short PROPERTY = 30;
    public static final short PLUS = 31;
    public static final short FORWARD_SLASH = 32;
    public static final short MINUS = 33;
    public static final short RPAREN = 34;
    public static final short IMPORTANT = 35;
    public static final short GREATER = 36;
    public static final short EQUAL = 37;
    public static final short INCLUDES = 38;
    public static final short DASHMATCH = 39;
    public static final short COMMENT = 40;

    private static final short MAXIMUM = 40;

    @SuppressWarnings("nls")
    private static final String[] NAMES = { "EOF", "IDENTIFIER", "COLOR", "COLON", "RCURLY",
            "SEMICOLON", "LBRACKET", "CLASS", "HASH", "STRING", "STAR", "SELECTOR", "FUNCTION",
            "URL", "LCURLY", "COMMA", "NUMBER", "PERCENTAGE", "LENGTH", "EMS", "EXS", "ANGLE",
            "TIME", "FREQUENCY", "PAGE", "AT_KEYWORD", "CHARSET", "MEDIA", "RBRACKET", "IMPORT",
            "PROPERTY", "PLUS", "FORWARD_SLASH", "MINUS", "RPAREN", "IMPORTANT", "GREATER",
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
