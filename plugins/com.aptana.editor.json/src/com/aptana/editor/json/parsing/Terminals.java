package com.aptana.editor.json.parsing;

/**
 * This class lists terminals used by the
 * grammar specified in "JSON.beaver".
 */
public class Terminals {
	static public final short EOF = 0;
	static public final short LCURLY = 1;
	static public final short LBRACKET = 2;
	static public final short NUMBER = 3;
	static public final short TRUE = 4;
	static public final short FALSE = 5;
	static public final short NULL = 6;
	static public final short STRING_DOUBLE = 7;
	static public final short STRING_SINGLE = 8;
	static public final short RCURLY = 9;
	static public final short PROPERTY = 10;
	static public final short RBRACKET = 11;
	static public final short COMMA = 12;
	static public final short COLON = 13;
}
