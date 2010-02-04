package com.aptana.editor.html.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class HTMLTokens
{
	public static final short UNKNOWN = -1;
	static public final short EOF = 0;
	static public final short STYLE = 1;
	static public final short SCRIPT = 2;
	static public final short START_TAG = 3;
	static public final short SELF_CLOSING = 4;
	static public final short TEXT = 5;
	static public final short END_TAG = 6;
	public static final short XML_DECL = 7;
	public static final short STRING = 8;
	public static final short COMMENT = 9;
	public static final short DOCTYPE = 10;

	private static final short MAXIMUM = 10;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "STYLE", "SCRIPT", "START_TAG", "SELF_CLOSING", "TEXT", "END_TAG",
			"XML_DECL", "STRING", "COMMENT", "DOCTYPE" };
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

	private HTMLTokens()
	{
	}
}
