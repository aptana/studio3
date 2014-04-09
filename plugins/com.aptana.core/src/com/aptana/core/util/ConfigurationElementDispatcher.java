/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;

/**
 * ConfigurationElementDispatcher
 */
public class ConfigurationElementDispatcher implements IConfigurationElementProcessor
{
	private Map<String, IConfigurationElementProcessor> dispatchTable;

	/**
	 * ConfigurationElementDispatcher
	 */
	public ConfigurationElementDispatcher()
	{
	}

	/**
	 * ConfigurationElementDispatcher
	 * 
	 * @param processors
	 */
	public ConfigurationElementDispatcher(IConfigurationElementProcessor... processors)
	{
		if (processors != null)
		{
			for (IConfigurationElementProcessor processor : processors)
			{
				addElementProcessor(processor);
			}
		}
	}

	/**
	 * addElementProcessor
	 * 
	 * @param processor
	 */
	private void addElementProcessor(IConfigurationElementProcessor processor)
	{
		if (processor != null)
		{
			if (dispatchTable == null)
			{
				dispatchTable = new HashMap<String, IConfigurationElementProcessor>();
			}

			for (String elementName : processor.getSupportElementNames())
			{
				dispatchTable.put(elementName, processor);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.aptana.core.util.IConfigurationElementProcessor#processElement(org.eclipse.core.runtime.IConfigurationElement
	 * )
	 */
	public void processElement(IConfigurationElement element)
	{
		String name = element.getName();

		if (dispatchTable != null && dispatchTable.containsKey(name))
		{
			dispatchTable.get(name).processElement(element);
		}
	}

	/**
	 * removeElementProcessor
	 * 
	 * @param elementName
	 */
	public void removeElementProcessor(String elementName)
	{
		if (dispatchTable != null && dispatchTable.containsKey(elementName))
		{
			dispatchTable.remove(elementName);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.IConfigurationElementProcessor#getSupportElementNames()
	 */
	public Set<String> getSupportElementNames()
	{
		if (dispatchTable != null)
		{
			return dispatchTable.keySet();
		}

		return Collections.emptySet();
	}
}