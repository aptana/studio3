package com.aptana.editor.xml.parsing.lexer;

import java.util.HashMap;
import java.util.Map;

public enum XMLToken
{
	EOF, COMMENT, STRING, CDATA, DECLARATION, START_TAG, END_TAG, TEXT;

	private static Map<Short, XMLToken> fTokens = new HashMap<Short, XMLToken>();
	static
	{
		for (XMLToken token : XMLToken.values())
		{
			fTokens.put(token.getIndex(), token);
		}
	}

	public short getIndex()
	{
		return (short) ordinal();
	}

	public static XMLToken getToken(short index)
	{
		return fTokens.get(index);
	}
}
