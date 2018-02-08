/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.core.io.preferences;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.ide.core.io.CoreIOPlugin;

public class PreferenceUtils
{

	/**
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 * @return true if the new files and folders should get their permissions updated after transferring, false
	 *         otherwise
	 */
	public static boolean getUpdatePermissions(PermissionDirection direction)
	{
		switch (direction)
		{
			case UPLOAD:
				return Platform.getPreferencesService().getBoolean(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.UPLOAD_UPDATE_PERMISSIONS, true, null);
			case DOWNLOAD:
				return Platform.getPreferencesService().getBoolean(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.DOWNLOAD_UPDATE_PERMISSIONS, true, null);
		}
		return true;
	}

	/**
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 * @return true if the new files and folders should update their permissions to specific permissions after
	 *         transferring, false if they should maintain the source permissions
	 */
	public static boolean getSpecificPermissions(PermissionDirection direction)
	{
		switch (direction)
		{
			case UPLOAD:
				return Platform.getPreferencesService().getBoolean(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.UPLOAD_SPECIFIC_PERMISSIONS, true, null);
			case DOWNLOAD:
				return Platform.getPreferencesService().getBoolean(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.DOWNLOAD_SPECIFIC_PERMISSIONS, true, null);
		}
		return true;
	}

	/**
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 * @return the permissions for new files created when transferring
	 */
	public static long getFilePermissions(PermissionDirection direction)
	{
		switch (direction)
		{
			case UPLOAD:
				return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.UPLOAD_FILE_PERMISSION, PreferenceInitializer.DEFAULT_FILE_PERMISSIONS,
						null);
			case DOWNLOAD:
				return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.DOWNLOAD_FILE_PERMISSION, PreferenceInitializer.DEFAULT_FILE_PERMISSIONS,
						null);
		}
		return PreferenceInitializer.DEFAULT_FILE_PERMISSIONS;
	}

	/**
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 * @return the permissions for new folders created when transferring
	 */
	public static long getFolderPermissions(PermissionDirection direction)
	{
		switch (direction)
		{
			case UPLOAD:
				return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.UPLOAD_FOLDER_PERMISSION,
						PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS, null);
			case DOWNLOAD:
				return Platform.getPreferencesService().getLong(CoreIOPlugin.PLUGIN_ID,
						IPreferenceConstants.DOWNLOAD_FOLDER_PERMISSION,
						PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS, null);
		}
		return PreferenceInitializer.DEFAULT_DIRECTORY_PERMISSIONS;
	}

	/**
	 * Sets if the new files and folders should get their permissions updated after transferring.
	 * 
	 * @param shouldUpdate
	 *            true if the permissions should be updated, false otherwise
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 */
	public static void setUpdatePermissions(boolean shouldUpdate, PermissionDirection direction)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CoreIOPlugin.PLUGIN_ID);
		switch (direction)
		{
			case UPLOAD:
				prefs.putBoolean(IPreferenceConstants.UPLOAD_UPDATE_PERMISSIONS, shouldUpdate);
				break;
			case DOWNLOAD:
				prefs.putBoolean(IPreferenceConstants.DOWNLOAD_UPDATE_PERMISSIONS, shouldUpdate);
				break;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
	}

	/**
	 * Sets if the new files and folders should update their permissions to specific permissions after transferring.
	 * 
	 * @param shouldSpecific
	 *            true if the permissions should be updated, false otherwise
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 */
	public static void setSpecificPermissions(boolean shouldSpecific, PermissionDirection direction)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CoreIOPlugin.PLUGIN_ID);
		switch (direction)
		{
			case UPLOAD:
				prefs.putBoolean(IPreferenceConstants.UPLOAD_SPECIFIC_PERMISSIONS, shouldSpecific);
				break;
			case DOWNLOAD:
				prefs.putBoolean(IPreferenceConstants.DOWNLOAD_SPECIFIC_PERMISSIONS, shouldSpecific);
				break;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
	}

	/**
	 * Sets the specific permissions used for new files created when transferring.
	 * 
	 * @param permissions
	 *            permissions in decimal form
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 */
	public static void setFilePermissions(long permissions, PermissionDirection direction)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CoreIOPlugin.PLUGIN_ID);
		switch (direction)
		{
			case UPLOAD:
				prefs.putLong(IPreferenceConstants.UPLOAD_FILE_PERMISSION, permissions);
				break;
			case DOWNLOAD:
				prefs.putLong(IPreferenceConstants.DOWNLOAD_FILE_PERMISSION, permissions);
				break;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
	}

	/**
	 * Sets the specific permissions used for new folders created when transferring.
	 * 
	 * @param permissions
	 *            permissions in decimal form
	 * @param direction
	 *            indicates if this is for upload or download permissions
	 */
	public static void setFolderPermissions(long permissions, PermissionDirection direction)
	{
		IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(CoreIOPlugin.PLUGIN_ID);
		switch (direction)
		{
			case UPLOAD:
				prefs.putLong(IPreferenceConstants.UPLOAD_FOLDER_PERMISSION, permissions);
				break;
			case DOWNLOAD:
				prefs.putLong(IPreferenceConstants.DOWNLOAD_FOLDER_PERMISSION, permissions);
				break;
		}
		try
		{
			prefs.flush();
		}
		catch (BackingStoreException e)
		{
			IdeLog.logError(CoreIOPlugin.getDefault(), e);
		}
	}
}
