/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.eclipse.core.runtime.IStatus;

/**
 * An {@link IStatusCollectorListener} should be registered in the {@link StatusCollector} in order to receive
 * status-change notifications.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public interface IStatusCollectorListener
{

	/**
	 * A notification of an {@link IStatus} change.
	 * 
	 * @param oldStatus
	 *            The old status (can be null in case a new status was added, and didn't replace any previous one that
	 *            was assigned to the same status-key).
	 * @param newStatus
	 *            The new status (can be null in case a status was removed from the {@link StatusCollector}.
	 */
	void statusChanged(IStatus oldStatus, IStatus newStatus);
}
