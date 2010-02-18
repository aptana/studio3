package com.aptana.editor.js.parsing;

import com.aptana.parsing.IParser;

public class JSParserFactory
{

	private static JSParserFactory fInstance;

	private IParser fParser;

	public static JSParserFactory getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new JSParserFactory();
		}

		return fInstance;
	}

	public IParser getParser()
	{
		return fParser;
	}

	private JSParserFactory()
	{
		fParser = new JSParser();
	}
}
