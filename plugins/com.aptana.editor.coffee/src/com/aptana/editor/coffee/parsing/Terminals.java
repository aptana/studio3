package com.aptana.editor.coffee.parsing;

import java.lang.reflect.Field;

/**
 * This class lists terminals used by the grammar specified in "coffee.grammar".
 */
public class Terminals
{
	static public final short EOF = 0;
	static public final short MINUS = 1;
	static public final short PLUS = 2;
	static public final short WHILE = 3;
	static public final short UNTIL = 4;
	static public final short FOR = 5;
	static public final short IDENTIFIER = 6;
	static public final short AT_SIGIL = 7;
	static public final short LCURLY = 8;
	static public final short LBRACKET = 9;
	static public final short NUMBER = 10;
	static public final short STRING = 11;
	static public final short LPAREN = 12;
	static public final short JS = 13;
	static public final short REGEX = 14;
	static public final short BOOL = 15;
	static public final short SUPER = 16;
	static public final short THIS = 17;
	static public final short HERECOMMENT = 18;
	static public final short IF = 19;
	static public final short FUNC_ARROW = 20;
	static public final short BOUND_FUNC_ARROW = 21;
	static public final short MINUS_MINUS = 22;
	static public final short PLUS_PLUS = 23;
	static public final short SWITCH = 24;
	static public final short PARAM_START = 25;
	static public final short CLASS = 26;
	static public final short TRY = 27;
	static public final short STATEMENT = 28;
	static public final short RETURN = 29;
	static public final short THROW = 30;
	static public final short LOOP = 31;
	static public final short UNARY = 32;
	static public final short QUESTION = 33;
	static public final short MATH = 34;
	static public final short INDENT = 35;
	static public final short SHIFT = 36;
	static public final short RELATION = 37;
	static public final short COMPARE = 38;
	static public final short LOGIC = 39;
	static public final short POST_IF = 40;
	static public final short OUTDENT = 41;
	static public final short CALL_START = 42;
	static public final short TERMINATOR = 43;
	static public final short INDEX_START = 44;
	static public final short INDEX_SOAK = 45;
	static public final short INDEX_PROTO = 46;
	static public final short COMMA = 47;
	static public final short DOT = 48;
	static public final short QUESTION_DOT = 49;
	static public final short DOUBLE_COLON = 50;
	static public final short FUNC_EXIST = 51;
	static public final short INDEX_END = 52;
	static public final short WHEN = 53;
	static public final short RBRACKET = 54;
	static public final short ELLIPSIS = 55;
	static public final short LEADING_WHEN = 56;
	static public final short ELSE = 57;
	static public final short EXTENDS = 58;
	static public final short DOT_DOT = 59;
	static public final short RPAREN = 60;
	static public final short EQUAL = 61;
	static public final short BY = 62;
	static public final short CALL_END = 63;
	static public final short FINALLY = 64;
	static public final short COLON_SLASH = 65;
	static public final short FORIN = 66;
	static public final short COMPOUND_ASSIGN = 67;
	static public final short PARAM_END = 68;
	static public final short RCURLY = 69;
	static public final short OWN = 70;
	static public final short FOROF = 71;
	static public final short COLON = 72;
	static public final short CATCH = 73;
	// Added manually by me after generation!
	public static final short THEN = 74;
	public static final short NEW = 75;

	/**
	 * Get the Terminal name/constant for the given id. i.e. RCURLY.
	 * 
	 * @param value
	 * @return
	 */
	public static String getNameForValue(short value)
	{
		for (Field f : Terminals.class.getFields())
		{
			try
			{
				Object fieldValue = f.get(null);
				if (fieldValue.equals(value))
				{
					return f.getName();
				}
			}
			catch (Exception e)
			{
				// ignore
			}
		}
		return Short.toString(value);
	}

	/**
	 * Used for printing out tokens to match coffee -t.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("nls")
	public static String getTokenID(short id)
	{
		switch (id)
		{
			case Terminals.AT_SIGIL:
				return "@";
			case Terminals.EQUAL:
				return "=";
			case Terminals.COLON:
				return ":";
			case Terminals.DOUBLE_COLON:
				return "::";
			case Terminals.QUESTION:
				return "?";
			case Terminals.QUESTION_DOT:
				return "?.";
			case Terminals.FUNC_ARROW:
				return "->";
			case Terminals.BOUND_FUNC_ARROW:
				return "=>";
			case Terminals.DOT:
				return ".";
			case Terminals.DOT_DOT:
				return "..";
			case Terminals.ELLIPSIS:
				return "...";
			case Terminals.LCURLY:
				return "{";
			case Terminals.RCURLY:
				return "}";
			case Terminals.LPAREN:
				return "(";
			case Terminals.RPAREN:
				return ")";
			case Terminals.LBRACKET:
				return "[";
			case Terminals.RBRACKET:
				return "]";
			case Terminals.MINUS:
				return "-";
			case Terminals.PLUS:
				return "+";
			case Terminals.COMMA:
				return ",";

			default:
				return null;
		}
	}

	/**
	 * Given a Terminal type, return the text value commonly associated with it. i.e. "}" for RCURLY This is used to
	 * populate generated tokens with correct values when all we have is a Terminal type.
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("nls")
	public static String getValue(short id)
	{
		String value = getTokenID(id);
		if (value != null)
		{
			return value;
		}
		switch (id)
		{
			case Terminals.CALL_START:
			case Terminals.PARAM_START:
				return "(";
			case Terminals.CALL_END:
			case Terminals.PARAM_END:
				return ")";
			default:
				return null;
		}
	}
}
