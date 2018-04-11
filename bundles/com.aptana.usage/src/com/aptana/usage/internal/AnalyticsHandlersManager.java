/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.IConfigurationElementProcessor;
import com.aptana.usage.IAnalyticsEventHandler;
import com.aptana.usage.UsagePlugin;

/**
 * An analytics handlers manager for loading {@link IAnalyticsEventHandler} extensions.
 * 
 * @author sgibly@appcelerator.com
 */
public class AnalyticsHandlersManager
{
	private static final String EXTENSION_POINT_ID = "analyticsHandlers"; //$NON-NLS-1$
	private static final String ELEMENT_HANDLER = "eventHandler"; //$NON-NLS-1$
	private static final String CLASS = "class"; //$NON-NLS-1$

	private static AnalyticsHandlersManager instance;
	private Set<IAnalyticsEventHandler> handlers;

	/**
	 * Returns a singleton instance of this {@link AnalyticsHandlersManager}.
	 * 
	 * @return an {@link AnalyticsHandlersManager} instance.
	 */
	public synchronized static AnalyticsHandlersManager getInstance()
	{
		if (instance == null)
		{
			instance = new AnalyticsHandlersManager();
		}
		return instance;
	}

	// Private constructor
	private AnalyticsHandlersManager()
	{
		loadExtensions();
	}

	/**
	 * Returns the loaded {@link Set} of {@link IAnalyticsEventHandler} instances.
	 * 
	 * @return The {@link IAnalyticsEventHandler} instances (read-only {@link Set}).
	 */
	public Set<IAnalyticsEventHandler> getHandlers()
	{
		return handlers;
	}

	/**
	 * Loads the handlers.
	 */
	private void loadExtensions()
	{
		final Set<IAnalyticsEventHandler> eventHandlers = new HashSet<IAnalyticsEventHandler>();
		EclipseUtil.processConfigurationElements(UsagePlugin.PLUGIN_ID, EXTENSION_POINT_ID,
				new IConfigurationElementProcessor()
				{

					public void processElement(IConfigurationElement element)
					{
						String name = element.getName();
						if (ELEMENT_HANDLER.equals(name))
						{
							try
							{
								eventHandlers.add((IAnalyticsEventHandler) element.createExecutableExtension(CLASS));
							}
							catch (CoreException e)
							{
								IdeLog.logError(UsagePlugin.getDefault(), "Error loading an analytics handler", e); //$NON-NLS-1$
							}
						}
					}

					public Set<String> getSupportElementNames()
					{
						return CollectionsUtil.newSet(ELEMENT_HANDLER);
					}
				});
		// Save the handlers in a read-only set.
		handlers = Collections.unmodifiableSet(eventHandlers);
	}
}
