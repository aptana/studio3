package com.aptana.xml.core.parsing;

/**
 * This class lists terminals used by the
 * grammar specified in "XML.grammar".
 */
public class Terminals {
	static public final short EOF = 0;
	static public final short LESS = 1;
	static public final short DECLARATION = 2;
	static public final short COMMENT = 3;
	static public final short IDENTIFIER = 4;
	static public final short GREATER = 5;
	static public final short TEXT = 6;
	static public final short CDATA = 7;
	static public final short SLASH_GREATER = 8;
	static public final short LESS_SLASH = 9;
	static public final short EQUAL = 10;
	static public final short STRING = 11;
	static public final short DOCTYPE = 12;
}
