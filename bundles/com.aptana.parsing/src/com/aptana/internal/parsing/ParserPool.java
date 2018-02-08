/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.internal.parsing;

import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.ReapingObjectPool;
import com.aptana.parsing.IParser;
import com.aptana.parsing.IParserPool;
import com.aptana.parsing.ParsingPlugin;

public class ParserPool extends ReapingObjectPool<IParser> implements IParserPool
{

	private IConfigurationElement parserExtension;

	public ParserPool(IConfigurationElement parserExtension)
	{
		this.parserExtension = parserExtension;
		start();
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
			IdeLog.logError(ParsingPlugin.getDefault(), e);
		}
		return null;
	}

	@Override
	public boolean validate(IParser o)
	{
		// Always keep one available parser
		return unlockedItems() == 1;
	}

	@Override
	public void expire(IParser o)
	{
		// no need to clean the parser up
	}
}
