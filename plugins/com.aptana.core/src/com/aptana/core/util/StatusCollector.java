/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SafeRunner;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * A collection of IStatus instances that may also notify changes to a list of registered listeners.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class StatusCollector
{

	private Map<Object, IStatus> statuses;
	private Set<IStatusCollectorListener> listeners;

	/**
	 * Constructs a new status collector.
	 */
	public StatusCollector()
	{
		statuses = new HashMap<Object, IStatus>();
		listeners = new HashSet<IStatusCollectorListener>(5);
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
		List<IStatus> result = new ArrayList<IStatus>();
		for (IStatus status : statuses.values())
		{
			if ((status.matches(severity)))
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
	int getStatusCount(int severity)
	{
		return getStatuses(severity).length;
	}

	/**
	 * Adds a {@link StatusCollector} listener.
	 * 
	 * @param listener
	 */
	public void addListener(IStatusCollectorListener listener)
	{
		if (listener != null)
		{
			listeners.add(listener);
		}
	}

	/**
	 * Removes a {@link StatusCollector} listener.
	 * 
	 * @param listener
	 */
	public void removeListener(IStatusCollectorListener listener)
	{
		if (listener != null)
		{
			listeners.remove(listener);
		}
	}

	/**
	 * Notify a status change to the registered listeners.
	 * 
	 * @param oldStatus
	 * @param newStatus
	 */
	private void notifyChange(final IStatus oldStatus, final IStatus newStatus)
	{
		IStatusCollectorListener[] notifyTo = listeners.toArray(new IStatusCollectorListener[listeners.size()]);
		for (final IStatusCollectorListener listener : notifyTo)
		{
			SafeRunner.run(new ISafeRunnable()
			{

				public void run() throws Exception
				{
					listener.statusChanged(oldStatus, newStatus);
				}

				public void handleException(Throwable exception)
				{
					IdeLog.logError(CorePlugin.getDefault(),
							"StatusCollector: Error while notifying a staus change event.", exception); //$NON-NLS-1$
				}
			});
		}
	}
}
