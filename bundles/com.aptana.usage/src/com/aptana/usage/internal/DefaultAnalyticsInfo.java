/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import com.aptana.usage.AnalyticsInfo;


public class DefaultAnalyticsInfo extends AnalyticsInfo
{

	private static final String NAME = "Aptana Studio"; //$NON-NLS-1$
	private static final String ID = "com.aptana.studio"; //$NON-NLS-1$
	private static final String GUID = "936afc9e-28ed-4618-88b8-f6e6e3869b09"; //$NON-NLS-1$
	private static final String VERSION_PLUGIN_ID = "com.aptana.branding"; //$NON-NLS-1$
	private static final String USER_AGENT = "AptanaStudio/3.1"; //$NON-NLS-1$

	public DefaultAnalyticsInfo()
	{
		super(NAME, ID, GUID, VERSION_PLUGIN_ID, USER_AGENT, null);
	}
}
