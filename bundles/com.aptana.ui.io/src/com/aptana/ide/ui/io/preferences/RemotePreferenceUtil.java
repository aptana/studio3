/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.ui.io.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.ui.io.IOUIPlugin;

public class RemotePreferenceUtil
{
	public static boolean getReopenRemoteOnStartup()
	{
		return Platform.getPreferencesService().getBoolean(IOUIPlugin.PLUGIN_ID,
				IPreferenceConstants.REOPEN_REMOTE_FILES_ON_STARUP, false, null);
	}

	public static void setReopenRemoteOnStartup(boolean reopen)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(IOUIPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.REOPEN_REMOTE_FILES_ON_STARUP, reopen);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
