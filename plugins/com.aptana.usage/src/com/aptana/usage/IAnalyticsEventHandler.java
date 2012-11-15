/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

/**
 * An {@link AnalyticsEvent} handler interface.
 * 
 * @author sgibly@appcelerator.com
 */
public interface IAnalyticsEventHandler
{
	/**
	 * Sends an {@link AnalyticsEvent}.
	 * 
	 * @param event
	 */
	void sendEvent(AnalyticsEvent event);

	/**
	 * Returns the analytics server URL.
	 * 
	 * @return A server URL
	 */
	String getAnalyticsURL();

	/**
	 * Returns the timout value the Studio will wait for a reply from the analytics server.
	 * 
	 * @return A timeout in milliseconds.
	 */
	int getTimeout();

	/**
	 * Returns the last response code from the Analytics server.
	 * 
	 * @return The last response code.
	 */
	int getLastResponseCode();

}
