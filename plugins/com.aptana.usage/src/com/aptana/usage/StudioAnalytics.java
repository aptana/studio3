/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.Set;

import org.eclipse.core.runtime.Platform;

import com.aptana.core.util.EclipseUtil;
import com.aptana.usage.internal.AnalyticsHandlersManager;

public class StudioAnalytics implements IStudioAnalytics
{

	/**
	 * @deprecated Use {@link UsagePlugin#getStudioAnalytics()}
	 * @return
	 */
	public static IStudioAnalytics getInstance()
	{
		UsagePlugin plugin = UsagePlugin.getDefault();
		if (plugin == null)
		{
			return new IStudioAnalytics()
			{
				public void sendEvent(AnalyticsEvent event)
				{
					// do nothing. We're shutting down. Unfortunately that means this event will get dropped.
				}
			};
		}
		return plugin.getStudioAnalytics();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IStudioAnalytics#sendEvent(com.aptana.usage.AnalyticsEvent)
	 */
	public void sendEvent(AnalyticsEvent event)
	{
		if (Platform.inDevelopmentMode() && !EclipseUtil.isTesting())
		{
			return;
		}
		// Cascade the event to all the registered handlers.
		Set<IAnalyticsEventHandler> handlers = AnalyticsHandlersManager.getInstance().getHandlers();
		for (IAnalyticsEventHandler handler : handlers)
		{
			handler.sendEvent(event);
		}
	}
}
