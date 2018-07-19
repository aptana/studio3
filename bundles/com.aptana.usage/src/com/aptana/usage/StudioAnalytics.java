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
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IScopeContext;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.usage.internal.AnalyticsHandlersManager;
import com.aptana.usage.preferences.IPreferenceConstants;

public class StudioAnalytics implements IStudioAnalytics
{

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IStudioAnalytics#sendEvent(com.aptana.usage.AnalyticsEvent)
	 */
	public void sendEvent(AnalyticsEvent event)
	{
		if (Platform.inDevelopmentMode() || EclipseUtil.isTesting() || !isUsageAnalyticsEnabled())
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

	private boolean isUsageAnalyticsEnabled()
	{
		IScopeContext configScope = ConfigurationScope.INSTANCE;
		boolean isStudioAnalyticsEnabled = Platform.getPreferencesService().getBoolean("com.aptana.ui", //$NON-NLS-1$
				IPreferenceConstants.ENABLE_STUDIO_USAGE_ANALYTICS, false, new IScopeContext[] { configScope });
		IdeLog.logInfo(UsagePlugin.getDefault(), "Is Studio usage analytics enabled: " + isStudioAnalyticsEnabled,
				UsagePlugin.PLUGIN_PREFERENCE_SCOPE);
		return isStudioAnalyticsEnabled;
	}
}
