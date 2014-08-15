/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.ui.preferences;

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.ide.syncing.ui.SyncingUIPlugin;
import com.aptana.ide.syncing.ui.preferences.IPreferenceConstants.SyncDirection;

public class SyncPreferenceUtil
{

	public static boolean isAutoSync(IProject project)
	{
		return Platform.getPreferencesService().getBoolean(SyncingUIPlugin.PLUGIN_ID,
				MessageFormat.format("{0}:{1}", IPreferenceConstants.AUTO_SYNC, project.getName()), false, null); //$NON-NLS-1$
	}

	public static SyncDirection getAutoSyncDirection(IProject project)
	{
		String type = Platform.getPreferencesService().getString(SyncingUIPlugin.PLUGIN_ID,
				MessageFormat.format("{0}:{1}", IPreferenceConstants.AUTO_SYNC_DIRECTION, project.getName()), null, //$NON-NLS-1$
				null);
		if (type != null)
		{
			if (type.equals(SyncDirection.UPLOAD.toString()))
			{
				return SyncDirection.UPLOAD;
			}
			if (type.equals(SyncDirection.DOWNLOAD.toString()))
			{
				return SyncDirection.DOWNLOAD;
			}
			if (type.equals(SyncDirection.BOTH.toString()))
			{
				return SyncDirection.BOTH;
			}
		}
		return null;
	}

	public static void setAutoSync(IProject project, boolean autoSync)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(SyncingUIPlugin.PLUGIN_ID);
		prefs.putBoolean(MessageFormat.format("{0}:{1}", IPreferenceConstants.AUTO_SYNC, project.getName()), autoSync); //$NON-NLS-1$
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}

	public static void setAutoSyncDirection(IProject project, SyncDirection direction)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(SyncingUIPlugin.PLUGIN_ID);
		prefs.put(MessageFormat.format("{0}:{1}", IPreferenceConstants.AUTO_SYNC_DIRECTION, project.getName()), //$NON-NLS-1$
				direction.toString());
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
		}
	}
}
