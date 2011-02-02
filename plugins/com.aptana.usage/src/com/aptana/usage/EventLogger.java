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
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kevin Lindsey
 */
public final class EventLogger
{

	private static final String TABLE_NAME = "eventLog"; //$NON-NLS-1$
	private static final String DATE_TIME = "dateTime"; //$NON-NLS-1$
	private static final String EVENT_TYPE = "eventType"; //$NON-NLS-1$
	private static final String MESSAGE = "message"; //$NON-NLS-1$

	private static final String GET_EVENTS = MessageFormat.format("SELECT {0},{1},{2} FROM {3} ORDER BY {0}", //$NON-NLS-1$
			new Object[] { DATE_TIME, EVENT_TYPE, MESSAGE, TABLE_NAME });

	private static EventLogger INSTANCE;

	private EventLogger()
	{
	}

	public static EventLogger getInstance()
	{
		if (INSTANCE == null)
		{
			INSTANCE = new EventLogger();

			if (!AptanaDB.tableExists(TABLE_NAME))
			{
				// builds the table-creation query
				String query = MessageFormat.format("CREATE TABLE {0}({1} bigint,{2} varchar(255),{3} varchar(255))", //$NON-NLS-1$
						new Object[] { TABLE_NAME, DATE_TIME, EVENT_TYPE, MESSAGE });

				// creates table
				AptanaDB.getInstance().execute(query);
			}
		}
		return INSTANCE;
	}

	public void logEvent(String eventType)
	{
		logEvent(eventType, null);
	}

	public void logEvent(String eventType, String message)
	{
		String dateTime = Long.toString(System.currentTimeMillis());
		String query = MessageFormat.format("INSERT INTO {0}({1},{2},{3}) VALUES({4},''{5}'',''{6}'')", //$NON-NLS-1$
				new Object[] { TABLE_NAME, DATE_TIME, EVENT_TYPE, MESSAGE, dateTime,
						(eventType != null && eventType.length() > 0) ? eventType : LogEventTypes.UNKNOWN,
						(message != null) ? message : "" //$NON-NLS-1$
				});

		AptanaDB.getInstance().execute(query);
	}

	public void clearEvents()
	{
		String query = "DELETE FROM " + TABLE_NAME; //$NON-NLS-1$
		AptanaDB.getInstance().execute(query);
	}

	public void clearEvents(String eventType)
	{
		String query = MessageFormat.format("DELETE FROM {0} WHERE {1}=''{2}''", //$NON-NLS-1$
				new Object[] { TABLE_NAME, EVENT_TYPE, eventType });
		AptanaDB.getInstance().execute(query);
	}

	public void clearEvents(long beforeTime)
	{
		String query = MessageFormat.format("DELETE FROM {0} WHERE {1} < {2}", //$NON-NLS-1$
				new Object[] { TABLE_NAME, DATE_TIME, Long.toString(beforeTime) });
		AptanaDB.getInstance().execute(query);
	}

	public void clearEvents(String eventType, long beforeTime)
	{
		String query = MessageFormat.format("DELETE FROM {0} WHERE {1}=''{2}'' AND {3} < {4}", //$NON-NLS-1$
				new Object[] { TABLE_NAME, EVENT_TYPE, eventType, DATE_TIME, Long.toString(beforeTime) });
		AptanaDB.getInstance().execute(query);
	}

	public EventInfo[] getEvents()
	{
		final List<EventInfo> events = new LinkedList<EventInfo>();

		AptanaDB.getInstance().execute(GET_EVENTS, new IResultSetHandler()
		{
			public void processResultSet(ResultSet resultSet) throws SQLException
			{
				long dateTime = resultSet.getLong(1);
				String eventType = resultSet.getString(2);
				String message = resultSet.getString(3);

				events.add(new EventInfo(dateTime, eventType, message));
			}
		});

		return events.toArray(new EventInfo[events.size()]);
	}

	public EventInfo[] getEvents(String eventType)
	{
		String query = MessageFormat.format("SELECT {0},{1},{2} FROM {3} WHERE {1}=''{4}'' ORDER BY {0}", //$NON-NLS-1$
				new Object[] { DATE_TIME, EVENT_TYPE, MESSAGE, TABLE_NAME, eventType });
		final List<EventInfo> events = new LinkedList<EventInfo>();

		AptanaDB.getInstance().execute(query, new IResultSetHandler()
		{
			public void processResultSet(ResultSet resultSet) throws SQLException
			{
				long dateTime = resultSet.getLong(1);
				String eventType = resultSet.getString(2);
				String message = resultSet.getString(3);

				events.add(new EventInfo(dateTime, eventType, message));
			}
		});

		return events.toArray(new EventInfo[events.size()]);
	}
}
