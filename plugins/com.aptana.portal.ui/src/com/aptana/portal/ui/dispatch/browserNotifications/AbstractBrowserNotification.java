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
package com.aptana.portal.ui.dispatch.browserNotifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.aptana.portal.ui.dispatch.BrowserNotifier;

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
		BrowserNotifier.getInstance().notifyBrowser(getNotificationTargets(), eventId, eventType, data);
	}
}
