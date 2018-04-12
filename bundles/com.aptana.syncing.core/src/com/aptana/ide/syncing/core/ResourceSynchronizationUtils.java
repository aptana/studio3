/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ide.syncing.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.StringUtil;

/**
 * This is a utility class for synchronization related functionality and settings for containers (i.e. projects and
 * resource folders).
 * 
 * @author Sandip V. Chitale (schitale@aptana.com)
 */
public class ResourceSynchronizationUtils
{

	public static final String LAST_SYNC_CONNECTION_KEY = "lastSyncConnection"; //$NON-NLS-1$
	public static final String REMEMBER_DECISION_KEY = "rememberDecision"; //$NON-NLS-1$

	private static final QualifiedName REMEMBER_DECISION = new QualifiedName(StringUtil.EMPTY,
			ResourceSynchronizationUtils.REMEMBER_DECISION_KEY);
	private static final QualifiedName LAST_SYNC_CONNECTION = new QualifiedName(StringUtil.EMPTY,
			ResourceSynchronizationUtils.LAST_SYNC_CONNECTION_KEY);

	/**
	 * Returns the value of "Remember my decision" setting which indicate whether to show the Choose Synchronization
	 * connection dialog when multiple connections are associated with the container.
	 * 
	 * @param container
	 * @return
	 * @throws NullPointerException
	 *             if the specified container is null
	 */
	public static boolean isRememberDecision(IContainer container)
	{
		if (container == null)
		{
			throw new NullPointerException("Null resource container."); //$NON-NLS-1$
		}

		try
		{
			return container.isAccessible() && Boolean.parseBoolean(container.getPersistentProperty(REMEMBER_DECISION));
		}
		catch (CoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), "Failed to retrieve the setting for \"remember my decision\"", e); //$NON-NLS-1$
		}
		return false;
	}

	/**
	 * Sets the value of "Remember my decision" setting which indicate whether to show the Choose Synchronization
	 * connection dialog when multiple connections are associated with the container.
	 * 
	 * @param container
	 * @param rememberMyDecision
	 * @throws NullPointerException
	 *             if the specified container is null
	 */
	public static void setRememberDecision(IContainer container, boolean rememberMyDecision)
	{
		if (container == null)
		{
			throw new NullPointerException("Null resource container."); //$NON-NLS-1$
		}

		try
		{
			if (container.isAccessible())
			{
				container.setPersistentProperty(REMEMBER_DECISION, String.valueOf(rememberMyDecision));
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(), "Failed to set the setting of \"remember my decision\"", e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the last synchronization connection in a serialized form.
	 * 
	 * @param container
	 * @return the last synchronization connection in a serialized form.
	 * @throws NullPointerException
	 *             if the specified container is null
	 */
	public static String getLastSyncConnection(IContainer container)
	{
		if (container == null)
		{
			throw new NullPointerException("Null resource container."); //$NON-NLS-1$
		}

		try
		{
			if (container.isAccessible())
			{
				return container.getPersistentProperty(LAST_SYNC_CONNECTION);
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(),
					"Failed to retrieve the setting for the last synchronization connection", e); //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * Sets the value of last synchronization connection.
	 * 
	 * @param container
	 * @param connection
	 *            a string representing the last synchronization connection. A <code>null</code> or <code>""</code>
	 *            removes the persistent setting.
	 * @throws NullPointerException
	 *             if the specified container is null
	 */
	public static void setLastSyncConnection(IContainer container, String connection)
	{
		if (container == null)
		{
			throw new NullPointerException("Null resource container."); //$NON-NLS-1$
		}

		try
		{
			if (container.isAccessible())
			{
				if (StringUtil.isEmpty(connection))
				{
					container.setPersistentProperty(LAST_SYNC_CONNECTION, null);
				}
				else
				{
					container.setPersistentProperty(LAST_SYNC_CONNECTION, connection);
				}
			}
		}
		catch (CoreException e)
		{
			IdeLog.logError(CorePlugin.getDefault(),
					"Failed to set the setting of the last synchronization connection", e); //$NON-NLS-1$
		}
	}
}
