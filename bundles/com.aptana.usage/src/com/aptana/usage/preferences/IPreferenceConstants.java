/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.preferences;

import com.aptana.usage.UsagePlugin;

public interface IPreferenceConstants
{
	public static final String AXWAY_GDPR_URL = "https://www.axway.com/gdpr"; //$NON-NLS-1$

	public static final String P_IDE_ID = "ide-id"; //$NON-NLS-1$

	public static final String P_IDE_HAS_RUN = "ide-has-run"; //$NON-NLS-1$

	public static final String HAS_ENROLLED = "has_enrolled"; //$NON-NLS-1$

	/**
	 * Control on/off studio analytics
	 */
	public static final String ENABLE_STUDIO_USAGE_ANALYTICS = UsagePlugin.PLUGIN_ID + ".enableStudioUsageAnalytics"; //$NON-NLS-1$

	/**
	 * Prompt the configuration dialog only once
	 */
	public static final String IS_STUDIO_USAGE_CONFIGURED = UsagePlugin.PLUGIN_ID + ".isStudioUsageConfigured"; //$NON-NLS-1$
}
