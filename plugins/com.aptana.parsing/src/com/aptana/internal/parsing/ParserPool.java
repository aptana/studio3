package com.aptana.internal.parsing;

import com.aptana.core.util.ObjectPool;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParsingPlugin;

public class ParserPool extends ObjectPool<IParser> implements IParserPool
{

	private String className;

	public ParserPool(String className)
	{
		super(-1);
		this.className = className;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public IParser create()
	{
		try
		{
			Class klass = Class.forName(className);
			return (IParser) klass.newInstance();
		}
		catch (Exception e)
		{
			ParsingPlugin.logError(e);
		}
		return null;
	}

	@Override
	public boolean validate(IParser o)
	{
		return true;
	}

	@Override
	public void expire(IParser o)
	{
		// no need to clean the parser up
	}

}
