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
		// TODO Use an object pool! parsers are expensive to instantiate and re-using same instance makes us queue up because of sync lock
		return fParser;
	}

	private JSParserFactory()
	{
		fParser = new JSParser();
	}
}
