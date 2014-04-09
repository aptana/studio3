/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.aptana.usage.internal.AptanaDB;

public class AnalyticsLogger
{

	private static final String TABLE_NAME = "analyticsLogs"; //$NON-NLS-1$
	private static final String DATE_TIME = "dateTime"; //$NON-NLS-1$
	private static final String EVENT_TYPE = "eventType"; //$NON-NLS-1$
	private static final String EVENT_NAME = "eventName"; //$NON-NLS-1$
	private static final String DATA = "data"; //$NON-NLS-1$

	private static final String GET_EVENTS = MessageFormat.format("SELECT {0},{1},{2},{3} FROM {4} ORDER BY {0}", //$NON-NLS-1$
			new Object[] { EVENT_TYPE, EVENT_NAME, DATA, DATE_TIME, TABLE_NAME });

	private static AnalyticsLogger INSTANCE;

	public synchronized static AnalyticsLogger getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new AnalyticsLogger();
			if (!AptanaDB.tableExists(TABLE_NAME))
			{
				String query = MessageFormat.format(
						"CREATE TABLE {0}({1} varchar(255), {2} varchar(255), {3} varchar(32000), {4} varchar(255))", //$NON-NLS-1$
						new Object[] { TABLE_NAME, EVENT_TYPE, EVENT_NAME, DATA, DATE_TIME });
				AptanaDB.getInstance().execute(query);
			}
		}
		return INSTANCE;
	}

	public void logEvent(AnalyticsEvent event)
	{
		String query = MessageFormat.format(
				"INSERT INTO {0}({1},{2},{3}, {4}) VALUES(''{5}'',''{6}'',''{7}'',''{8}'')", //$NON-NLS-1$
				new Object[] { TABLE_NAME, EVENT_TYPE, EVENT_NAME, DATA, DATE_TIME, event.getEventType(),
						event.getEventName(), event.getJSONPayloadString(), event.getDateTime() });

		AptanaDB.getInstance().execute(query);
	}

	public void clearEvent(AnalyticsEvent event)
	{
		String query = MessageFormat.format(
				"DELETE FROM {0} WHERE {1}=''{2}'' AND {3}=''{4}'' AND {5}=''{6}'' AND {7}=''{8}''", //$NON-NLS-1$
				new Object[] { TABLE_NAME, EVENT_TYPE, event.getEventType(), EVENT_NAME, event.getEventName(), DATA,
						event.getJSONPayloadString(), DATE_TIME, event.getDateTime() });
		AptanaDB.getInstance().execute(query);
	}

	public List<AnalyticsEvent> getEvents()
	{
		final List<AnalyticsEvent> events = new ArrayList<AnalyticsEvent>();

		AptanaDB.getInstance().execute(GET_EVENTS, new IResultSetHandler()
		{

			public void processResultSet(ResultSet resultSet) throws SQLException
			{
				String eventType = resultSet.getString(1);
				String eventName = resultSet.getString(2);
				String data = resultSet.getString(3);
				String dateTime = resultSet.getString(4);

				events.add(new AnalyticsEvent(eventType, eventName, data, dateTime));
			}
		});

		return events;
	}
}
