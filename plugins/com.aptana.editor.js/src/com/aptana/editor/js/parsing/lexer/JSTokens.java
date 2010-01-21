package com.aptana.editor.js.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class JSTokens
{
	public static final short UNKNOWN = -1;
	static public final short EOF = 0;
	static public final short LPAREN = 1;
	static public final short IDENTIFIER = 2;
	static public final short LCURLY = 3;
	static public final short LBRACKET = 4;
	static public final short PLUS_PLUS = 5;
	static public final short MINUS_MINUS = 6;
	static public final short STRING = 7;
	static public final short NUMBER = 8;
	static public final short MINUS = 9;
	static public final short PLUS = 10;
	static public final short THIS = 11;
	static public final short NEW = 12;
	static public final short NULL = 13;
	static public final short TRUE = 14;
	static public final short FALSE = 15;
	static public final short REGEX = 16;
	static public final short DELETE = 17;
	static public final short EXCLAMATION = 18;
	static public final short TILDE = 19;
	static public final short TYPEOF = 20;
	static public final short VOID = 21;
	static public final short FUNCTION = 22;
	static public final short SEMICOLON = 23;
	static public final short COMMA = 24;
	static public final short VAR = 25;
	static public final short WHILE = 26;
	static public final short FOR = 27;
	static public final short DO = 28;
	static public final short IF = 29;
	static public final short CONTINUE = 30;
	static public final short BREAK = 31;
	static public final short WITH = 32;
	static public final short SWITCH = 33;
	static public final short RETURN = 34;
	static public final short THROW = 35;
	static public final short TRY = 36;
	static public final short RPAREN = 37;
	static public final short ELSE = 38;
	static public final short RCURLY = 39;
	static public final short COLON = 40;
	static public final short RBRACKET = 41;
	static public final short IN = 42;
	static public final short EQUAL = 43;
	static public final short CASE = 44;
	static public final short DOT = 45;
	static public final short LESS_LESS = 46;
	static public final short GREATER_GREATER = 47;
	static public final short GREATER_GREATER_GREATER = 48;
	static public final short LESS = 49;
	static public final short GREATER = 50;
	static public final short LESS_EQUAL = 51;
	static public final short GREATER_EQUAL = 52;
	static public final short INSTANCEOF = 53;
	static public final short EQUAL_EQUAL = 54;
	static public final short EXCLAMATION_EQUAL = 55;
	static public final short EQUAL_EQUAL_EQUAL = 56;
	static public final short EXCLAMATION_EQUAL_EQUAL = 57;
	static public final short AMPERSAND = 58;
	static public final short CARET = 59;
	static public final short PIPE = 60;
	static public final short AMPERSAND_AMPERSAND = 61;
	static public final short STAR_EQUAL = 62;
	static public final short FORWARD_SLASH_EQUAL = 63;
	static public final short PERCENT_EQUAL = 64;
	static public final short PLUS_EQUAL = 65;
	static public final short MINUS_EQUAL = 66;
	static public final short LESS_LESS_EQUAL = 67;
	static public final short GREATER_GREATER_EQUAL = 68;
	static public final short GREATER_GREATER_GREATER_EQUAL = 69;
	static public final short AMPERSAND_EQUAL = 70;
	static public final short CARET_EQUAL = 71;
	static public final short PIPE_EQUAL = 72;
	static public final short STAR = 73;
	static public final short FORWARD_SLASH = 74;
	static public final short PERCENT = 75;
	static public final short QUESTION = 76;
	static public final short PIPE_PIPE = 77;
	static public final short DEFAULT = 78;
	static public final short FINALLY = 79;
	static public final short CATCH = 80;
	public static final short SINGLELINE_COMMENT = 81;
	public static final short MULTILINE_COMMENT = 82;
	public static final short DOC = 83;

	private static final short MAXIMUM = 84;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "(", "IDENTIFIER", "{", "[", "++", "--", "STRING", "NUMBER", "-",
			"+", "this", "new", "null", "true", "false", "REGEX", "delete", "!", "~", "typeof", "void", "function",
			";", ",", "var", "while", "for", "do", "if", "continue", "break", "with", "switch", "return", "throw",
			"try", ")", "else", "}", ":", "]", "in", "=", "case", ".", "<<", ">>", ">>>", "<", ">", "<=", ">=",
			"instanceof", "==", "!=", "===", "!==", "&", "^", "|", "&&", "*=", "/=", "%=", "+=", "-=", "<<=", ">>=",
			">>>=", "&=", "^=", "|=", "*", "/", "%", "?", "||", "default", "finally", "catch", "SINGLELINE_COMMENT",
			"MULTILINE_COMMENT", "DOC", };
	private static final String NAME_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

	private static Map<String, Short> nameIndexMap;

	public static String getTokenName(short token)
	{
		init();
		if (token < 0 || token > MAXIMUM)
		{
			return NAME_UNKNOWN;
		}
		return NAMES[token];
	}

	public static short getToken(String tokenName)
	{
		init();
		Short token = nameIndexMap.get(tokenName);
		return (token == null) ? UNKNOWN : token;
	}

	private static void init()
	{
		if (nameIndexMap == null)
		{
			nameIndexMap = new HashMap<String, Short>();
			short index = 0;
			for (String name : NAMES)
			{
				nameIndexMap.put(name, index++);
			}
		}
	}

	private JSTokens()
	{
	}
}
