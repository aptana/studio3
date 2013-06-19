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

public class StudioAnalytics
{

	private static StudioAnalytics instance;

	public synchronized static StudioAnalytics getInstance()
	{
		if (instance == null)
		{
			instance = new StudioAnalytics();
		}
		return instance;
	}

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
