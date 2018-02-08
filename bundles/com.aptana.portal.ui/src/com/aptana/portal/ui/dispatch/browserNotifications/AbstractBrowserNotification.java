/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.browserNotifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.portal.ui.dispatch.BrowserNotifier;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;

/**
 * A base class for all browser notification classed. The BrowserNotification register itself as a listener to some
 * eclipse event and then notify the target browser editors about any event that is occurring. The notification is done
 * through the {@link BrowserNotifier} class.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public abstract class AbstractBrowserNotification
{
	protected List<String> notificationTargets;
	protected boolean isListening;

	/**
	 * Constructor
	 */
	public AbstractBrowserNotification()
	{
		notificationTargets = new ArrayList<String>();
	}

	/**
	 * Set the notification targets for this browser notification class. An asterisk (*) sign signals to notify all the
	 * opened registered browser-editors.
	 * 
	 * @param targets
	 *            A list of comma separated target ids.
	 */
	public void setNotificationTargets(String targets)
	{
		notificationTargets.clear();
		if (targets != null)
		{
			String[] ids = targets.split(", *"); //$NON-NLS-1$
			for (String id : ids)
			{
				id = id.trim();
				if (id.equals("*")) { //$NON-NLS-1$
					// in case we have an asterisk, the notification will go to all
					// of the registered browser-editors.
					notificationTargets.clear();
					break;
				}
				if (id.length() > 0)
				{
					notificationTargets.add(id);
				}
			}
		}
	}

	/**
	 * Returns a list of notification targets. An empty list signals that we need to notify any available registered
	 * browser editor.
	 * 
	 * @return An unmodifiable notification targets list.
	 */
	public List<String> getNotificationTargets()
	{
		return Collections.unmodifiableList(notificationTargets);
	}

	/**
	 * Start this browser notifier.
	 */
	public abstract void start();

	/**
	 * Stop this browser notifier.
	 */
	public abstract void stop();

	/**
	 * Fire a notification for the registered notification targets with the eventId, type and data.
	 * 
	 * @param eventId
	 *            See {@link IBrowserNotificationConstants}
	 * @param eventType
	 *            See {@link IBrowserNotificationConstants}
	 * @param data
	 *            A JSON data (can be null)
	 * @see IBrowserNotificationConstants
	 */
	protected void notifyTargets(String eventId, String eventType, String data)
	{
		notifyTargets(eventId, eventType, data, false);
	}

	/**
	 * Fire a notification for the registered notification targets with the eventId, type and data.
	 * 
	 * @param eventId
	 *            See {@link IBrowserNotificationConstants}
	 * @param eventType
	 *            See {@link IBrowserNotificationConstants}
	 * @param data
	 *            A JSON data (can be null)
	 * @param notifyInUIThread
	 *            Indicate that the notification should be wrapped in a UI thread.
	 * @see IBrowserNotificationConstants
	 */
	protected void notifyTargets(String eventId, String eventType, String data, boolean notifyInUIThread)
	{
		if (notifyInUIThread)
		{
			BrowserNotifier.getInstance().notifyBrowserInUIThread(getNotificationTargets(), eventId, eventType, data);
		}
		else
		{
			BrowserNotifier.getInstance().notifyBrowser(getNotificationTargets(), eventId, eventType, data);
		}
	}
}
