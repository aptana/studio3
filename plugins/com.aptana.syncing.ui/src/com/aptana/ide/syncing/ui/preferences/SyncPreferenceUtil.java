/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
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
		IEclipsePreferences prefs = (new InstanceScope()).getNode(SyncingUIPlugin.PLUGIN_ID);
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
		IEclipsePreferences prefs = (new InstanceScope()).getNode(SyncingUIPlugin.PLUGIN_ID);
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
