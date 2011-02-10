/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

/**
 * @author Kevin Lindsey
 */
public final class EventInfo
{

	private final long _dateTime;
	private final String _eventType;
	private final String _message;

	public EventInfo(long dateTime, String eventType, String message)
	{
		_dateTime = dateTime;
		_eventType = eventType;
		_message = message;
	}

	public long getDateTime()
	{
		return _dateTime;
	}

	public String getEventType()
	{
		return _eventType;
	}

	public String getMessage()
	{
		return _message;
	}
}
