/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * A collection of IStatus instances that may also notify changes to a list of registered listeners.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class StatusCollection
{

	private Map<Object, IStatus> statuses;
	private List<IStatusCollectionListener> listeners;

	/**
	 * Constructs a new status collection.
	 */
	public StatusCollection()
	{
		statuses = new HashMap<Object, IStatus>();
		listeners = new ArrayList<IStatusCollectionListener>(5);
	}

	/**
	 * Set an IStatus for a given identifier.<br>
	 * Passing a <code>null</code> status to this method is like calling the {@link #clearStatus(Object)} method.
	 * 
	 * @param status
	 *            The status to set. Passing <code>null</code> here is the same as calling {@link #clearStatus(Object)}.
	 * @param id
	 * @see #clearStatus(Object)
	 */
	public void setStatus(IStatus status, Object id)
	{
		if (status == null)
		{
			clearStatus(id);
			return;
		}
		// Just override the status we have for the given ID.
		IStatus oldStatus = statuses.put(id, status);
		if (oldStatus == null || !oldStatus.equals(status))
		{
			notifyChange(oldStatus, status);
		}
	}

	/**
	 * Returns a registered {@link IStatus} for a given ID.
	 * 
	 * @param id
	 * @return A {@link IStatus}; <code>null</code> if no status was registered with the given ID.
	 */
	public IStatus getStatus(Object id)
	{
		return statuses.get(id);
	}

	/**
	 * Remove a status item from this indicator component.
	 * 
	 * @param id
	 *            The status identifier.
	 * @see #setStatus(IStatus, Object)
	 */
	public void clearStatus(Object id)
	{
		IStatus removed = statuses.remove(id);
		if (removed != null)
		{
			notifyChange(removed, null);
		}
	}

	/**
	 * Returns an array of {@link IStatus} instances that match the severity flag.
	 * 
	 * @param severity
	 *            An {@link IStatus} severity value that can also be built by a <em>bitwise OR</em> of several
	 *            severities together.
	 * @return An array of {@link IStatus} instances that this collection hold and match the requested severity.
	 */
	public IStatus[] getStatuses(int severity)
	{
		Collection<IStatus> values = statuses.values();
		List<IStatus> result = new ArrayList<IStatus>();
		for (IStatus status : values)
		{
			if ((status.getSeverity() & severity) != 0)
			{
				result.add(status);
			}
		}
		return result.toArray(new IStatus[result.size()]);
	}

	/**
	 * Returns the number of {@link IStatus} instances with the given severity.
	 * 
	 * @param severity
	 *            An {@link IStatus} severity value that can also be built by a <em>bitwise OR</em> of several
	 *            severities together.
	 * @return A status count.
	 */
	public int getStatusCount(int severity)
	{
		return getStatuses(severity).length;
	}

	/**
	 * Adds a {@link StatusCollection} listener.
	 * 
	 * @param listener
	 */
	public void addListener(IStatusCollectionListener listener)
	{
		listeners.add(listener);
	}

	/**
	 * Removes a {@link StatusCollection} listener.
	 * 
	 * @param listener
	 */
	public void removeListener(IStatusCollectionListener listener)
	{
		listeners.remove(listener);
	}

	/**
	 * Notify a status change to the registered listeners.
	 * 
	 * @param oldStatus
	 * @param newStatus
	 */
	private void notifyChange(IStatus oldStatus, IStatus newStatus)
	{
		IStatusCollectionListener[] notifyTo = listeners.toArray(new IStatusCollectionListener[listeners.size()]);
		for (IStatusCollectionListener listener : notifyTo)
		{
			listener.statusChanged(oldStatus, newStatus);
		}
	}
}
