package com.aptana.editor.common;

public class ParserPoolFactory
{

	private static ParserPoolFactory fgInstance;

	public IParserPool getParserPool(String language)
	{
		// TODO Allow plugins to register a parser/pool per language/MIME-type via extension point!
		return null;
	}

	public static ParserPoolFactory getInstance()
	{
		if (fgInstance == null)
		{
			fgInstance = new ParserPoolFactory();
		}
		return fgInstance;
	}

	private ParserPoolFactory()
	{
	}
}
