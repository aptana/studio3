package com.aptana.internal.parsing;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.util.ObjectPool;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParsingPlugin;

public class ParserPool extends ObjectPool<IParser> implements IParserPool
{

	private IConfigurationElement parserExtension;

	public ParserPool(IConfigurationElement parserExtension)
	{
		super(-1);
		this.parserExtension = parserExtension;
	}

	@Override
	public IParser create()
	{		
		try
		{
			return (IParser) parserExtension.createExecutableExtension("class"); //$NON-NLS-1$
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
