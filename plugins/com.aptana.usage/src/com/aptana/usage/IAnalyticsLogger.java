/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.List;

/**
 * Persists {@link AnalyticsEvent}s so that we can queue them up and reload them when we're having issues sending them
 * over the network. Typically this is backed by persisting to disk/prefs/DB.
 * 
 * @author cwilliams
 */
public interface IAnalyticsLogger
{

	/**
	 * Persists an event.
	 * 
	 * @param event
	 */
	public abstract void logEvent(AnalyticsEvent event);

	/**
	 * Removes all persisted events.
	 */
	public abstract void clearEvents();

	/**
	 * Removes persisted copy of the supplied event.
	 * 
	 * @param event
	 */
	public abstract void clearEvent(AnalyticsEvent event);

	/**
	 * Loads all the persisted events.
	 * 
	 * @return
	 */
	public abstract List<AnalyticsEvent> getEvents();

}