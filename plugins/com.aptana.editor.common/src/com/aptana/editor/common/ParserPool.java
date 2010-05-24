package com.aptana.editor.common;

import com.aptana.core.util.ObjectPool;
import com.aptana.parsing.IParser;

public class ParserPool extends ObjectPool<IParser> implements IParserPool
{

	private String className;

	ParserPool(String className)
	{
		this.className = className;
	}

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
			CommonEditorPlugin.logError(e);
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
