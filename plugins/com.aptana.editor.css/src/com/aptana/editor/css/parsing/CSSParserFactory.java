package com.aptana.editor.css.parsing;

import com.aptana.parsing.IParser;

public class CSSParserFactory
{

	private static CSSParserFactory fInstance;

	private IParser fParser;

	public static CSSParserFactory getInstance()
	{
		if (fInstance == null)
		{
			fInstance = new CSSParserFactory();
		}

		return fInstance;
	}

	public IParser getParser()
	{
		// TODO Use an object pool! parsers are expensive to instantiate and re-using same instance makes us queue up because of sync lock
		return fParser;
	}

	private CSSParserFactory()
	{
		fParser = new CSSParser();
	}
}
