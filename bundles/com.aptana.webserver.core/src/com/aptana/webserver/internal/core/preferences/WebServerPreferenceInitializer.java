/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.internal.core.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.webserver.core.WebServerCorePlugin;
import com.aptana.webserver.core.preferences.IWebServerPreferenceConstants;

/**
 * @author Max Stepanov
 */
public class WebServerPreferenceInitializer extends AbstractPreferenceInitializer
{

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences node = DefaultScope.INSTANCE.getNode(WebServerCorePlugin.PLUGIN_ID);
		node.put(IWebServerPreferenceConstants.PREF_HTTP_SERVER_ADDRESS,
				IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_ADDRESS);
		node.put(IWebServerPreferenceConstants.PREF_HTTP_SERVER_PORTS,
				IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_PORTS_RANGE[0]
						+ "-" + IWebServerPreferenceConstants.DEFAULT_HTTP_SERVER_PORTS_RANGE[1]); //$NON-NLS-1$
	}

}
