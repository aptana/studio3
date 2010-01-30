package com.aptana.editor.html.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class HTMLTokens
{
	public static final short UNKNOWN = -1;
	static public final short EOF = 0;
	static public final short START_TAG = 1;
	static public final short SELF_CLOSING = 2;
	static public final short TEXT = 3;
	static public final short END_TAG = 4;
	public static final short STRING = 4;
	public static final short COMMENT = 5;
	public static final short DOCTYPE = 6;
	public static final short SCRIPT = 7;
	public static final short STYLE = 8;

	private static final short MAXIMUM = 8;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "START_TAG", "SELF_CLOSING", "TEXT", "END_TAG", "STRING", "COMMENT", "DOCTYPE",
			"SCRIPT", "STYLE" };
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
