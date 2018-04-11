/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.editor.html.HTMLPlugin;
import com.aptana.editor.html.core.preferences.IPreferenceConstants;

public class HTMLPreferenceUtil
{

	public static boolean getShowTextNodesInOutline()
	{
		return Platform.getPreferencesService().getBoolean(HTMLPlugin.PLUGIN_ID,
				IPreferenceConstants.HTML_OUTLINE_SHOW_TEXT_NODES, false, null);
	}

	public static void setShowTextNodesInOutline(boolean show)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(HTMLPlugin.PLUGIN_ID);
		prefs.putBoolean(IPreferenceConstants.HTML_OUTLINE_SHOW_TEXT_NODES, show);
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(HTMLPlugin.getDefault(), e);
		}
	}
}
