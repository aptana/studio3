/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.util.Map;

import com.aptana.usage.AnalyticsEvent;

/**
 * A simple subclass with a hard-coded event type for feature events. So we don't have to pass around a special constant
 * everywhere.
 * 
 * @author cwilliams
 */
public class FeatureEvent extends AnalyticsEvent
{

	public FeatureEvent(String eventName, Map<String, String> eventPayload)
	{
		super("app.feature", eventName, eventPayload); //$NON-NLS-1$
	}

}
