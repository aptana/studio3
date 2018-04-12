/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.ftp.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.aptana.filesystem.ftp.FTPPlugin;

public class FTPPreferenceInitializer extends AbstractPreferenceInitializer
{

	public static final int DEFAULT_KEEP_ALIVE_MINUTES = 7;

	@Override
	public void initializeDefaultPreferences()
	{
		IEclipsePreferences prefs = DefaultScope.INSTANCE.getNode(FTPPlugin.PLUGIN_ID);
		prefs.putInt(IFTPPreferenceConstants.KEEP_ALIVE_TIME, DEFAULT_KEEP_ALIVE_MINUTES);
	}
}
