/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public class HTMLTokens
{
	public static final short UNKNOWN = -1;
	public static final short EOF = 0;
	public static final short STYLE = 1;
	public static final short SCRIPT = 2;
	public static final short START_TAG = 3;
	public static final short TEXT = 4;
	public static final short END_TAG = 5;
	public static final short XML_DECL = 6;
	public static final short STRING = 7;
	public static final short COMMENT = 8;
	public static final short DOCTYPE = 9;
	public static final short STYLE_END = 10;
	public static final short SCRIPT_END = 11;
	public static final short CDATA = 12;

	private static final short MAXIMUM = 12;

	@SuppressWarnings("nls")
	private static final String[] NAMES = { "EOF", "STYLE", "SCRIPT", "START_TAG", "TEXT", "END_TAG", "XML_DECL",
			"STRING", "COMMENT", "DOCTYPE", "STYLE_END", "SCRIPT_END", "CDATA" };
	private static final String NAME_UNKNOWN = "UNKNOWN"; //$NON-NLS-1$

	private static final Map<String, Short> nameIndexMap = new HashMap<String, Short>(NAMES.length, 0.3f);

	/*
	 * Initialize structure statically
	 */
	static
	{
		short index = 0;
		for (String name : NAMES)
		{
			nameIndexMap.put(name, index++);
		}
	}

	public static String getTokenName(short token)
	{
		if (token < 0 || token > MAXIMUM)
		{
			return NAME_UNKNOWN;
		}
		return NAMES[token];
	}

	public static short getToken(String tokenName)
	{
		Short token = nameIndexMap.get(tokenName);
		return (token == null) ? UNKNOWN : token;
	}

	private HTMLTokens()
	{
	}
}
