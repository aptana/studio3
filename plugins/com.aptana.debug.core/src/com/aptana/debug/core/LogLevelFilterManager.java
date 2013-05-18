/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.debug.core;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.core.util.StringUtil;

/**
 * Manages {@link ILogLevelFilter} contributions.
 * 
 * @author sgibly@appcelerator.com
 */
public class LogLevelFilterManager
{
	private static final String EXTENSION_POINT_ID = "logLevelFilters"; //$NON-NLS-1$
	private static final String ELEMENT_TYPE = "filter"; //$NON-NLS-1$
	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private Map<String, IConfigurationElement> filters;

	/**
	 * Constructs a new LogLevelFilterManager
	 */
	protected LogLevelFilterManager()
	{
		loadFilters();
	}

	/**
	 * Instantiate and return a new {@link ILogLevelFilter}.
	 * 
	 * @param filterID
	 * @param process
	 * @return An {@link ILogLevelFilter}; <code>null</code> if no filter with the given ID was contributed.
	 */
	public ILogLevelFilter getFilter(String filterID, IProcess process)
	{
		IConfigurationElement element = filters.get(filterID);
		if (element == null)
		{
			return null;
		}
		ILogLevelFilter filter = null;
		try
		{
			Object clazz = element.createExecutableExtension(ATTR_CLASS);
			if (clazz instanceof ILogLevelFilter)
			{
				filter = (ILogLevelFilter) clazz;
				filter.setInitializationData(element, ILogLevelFilter.PROCESS_PROPERTY_NAME, process);
				if (filter instanceof IDebugEventSetListener)
				{
					DebugPlugin.getDefault().addDebugEventListener((IDebugEventSetListener) filter);
				}
			}
			else
			{
				IdeLog.logError(
						DebugCorePlugin.getDefault(),
						MessageFormat.format(
								"The class {0} does not implement ILogLevelFilter", element.getAttribute(ATTR_CLASS))); //$NON-NLS-1$
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(DebugCorePlugin.getDefault(), e);
		}
		return filter;
	}

	/*
	 * Load the filters contributions.
	 */
	private void loadFilters()
	{
		filters = new HashMap<String, IConfigurationElement>();
		EclipseUtil.processConfigurationElements(DebugCorePlugin.PLUGIN_ID, EXTENSION_POINT_ID,
				new IConfigurationElementProcessor()
				{
					public void processElement(IConfigurationElement element)
					{
						String elementName = element.getName();
						if (ELEMENT_TYPE.equals(elementName))
						{
							String id = element.getAttribute(ATTR_ID);
							if (StringUtil.isEmpty(id))
							{
								return;
							}
							String className = element.getAttribute(ATTR_CLASS);
							if (StringUtil.isEmpty(className))
							{
								IdeLog.logError(DebugCorePlugin.getDefault(), "Missing an ILogLevelFilter class"); //$NON-NLS-1$
								return;
							}
							filters.put(id, element);
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_TYPE);
					}
				});
	}
}
