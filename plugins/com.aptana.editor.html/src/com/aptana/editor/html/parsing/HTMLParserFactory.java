package com.aptana.editor.html.parsing;

import com.aptana.parsing.IParser;

public class HTMLParserFactory
{

	private static HTMLParserFactory fInstance;

	private IParser fParser;

	public static HTMLParserFactory getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new HTMLParserFactory();
		}

		return fInstance;
	}

	public IParser getParser()
	{
		return fParser;
	}

	private HTMLParserFactory()
	{
		fParser = new HTMLParser();
	}
}
