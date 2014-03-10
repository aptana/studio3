/**
 * Aptana Studio
 * Copyright (c) 2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.usage.AnalyticsEvent;
import com.aptana.usage.AnalyticsLogger;
import com.aptana.usage.IAnalyticsEventHandler;
import com.aptana.usage.IAnalyticsUser;
import com.aptana.usage.IAnalyticsUserManager;
import com.aptana.usage.IDebugScopes;
import com.aptana.usage.IUsageSystemProperties;
import com.aptana.usage.Messages;
import com.aptana.usage.UsagePlugin;

/**
 * A default {@link AnalyticsEvent} handler.
 * 
 * @author sgibly@appcelerator.com
 */
public class DefaultAnalyticsEventHandler implements IAnalyticsEventHandler
{
	static final String DEFAULT_URL = "https://api.appcelerator.net/p/v1/app-track"; //$NON-NLS-1$
	static final int DEFAULT_TIMEOUT = 5 * 1000; // 5 seconds

	private final String url;
	private final int timeout;
	protected int responseCode = 0;
	protected Object lock = new Object();

	DefaultAnalyticsEventHandler()
	{
		this(DEFAULT_TIMEOUT, EclipseUtil.getSystemProperty(IUsageSystemProperties.ANALYTICS_URL));
	}

	DefaultAnalyticsEventHandler(int timeout, String url)
	{
		this.timeout = timeout;
		this.url = url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsEventHandler#sendEvent(com.aptana.usage.AnalyticsEvent)
	 */
	public void sendEvent(final AnalyticsEvent event)
	{
		Job job = new Job("Sending Analytics Ping ...") //$NON-NLS-1$
		{

			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				IAnalyticsUserManager userManager = AnalyticsEvent.getUserManager();
				if (userManager == null)
				{
					// send as anonymous user
					if (!isValidResponse(responseCode = sendPing(event, null)))
					{
						// log the event to the database
						AnalyticsLogger.getInstance().logEvent(event);
					}
					return Status.OK_STATUS;
				}

				IAnalyticsUser user = userManager.getUser();
				// Only send ping if user is logged in. Otherwise, we log it to the database
				if (user == null || !user.isOnline() || !isValidResponse(responseCode = sendPing(event, user)))
				{
					// log the event to the database
					AnalyticsLogger.getInstance().logEvent(event);
				}
				else
				{
					// Send out all previous events from the db
					synchronized (lock)
					{
						List<AnalyticsEvent> events = AnalyticsLogger.getInstance().getEvents();
						// Sort the events. We want all project.create events to be first, and all project.delete events
						// to be last
						Collections.sort(events, new AnalyticsEventComparator());
						for (AnalyticsEvent aEvent : events)
						{
							if (!isValidResponse(responseCode = sendPing(aEvent, user)))
							{
								return Status.OK_STATUS;
							}
							// Remove the event after it has been sent
							AnalyticsLogger.getInstance().clearEvent(aEvent);
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.BUILD);
		job.schedule();

		// Make this a blocking job for unit tests
		if (EclipseUtil.isTesting())
		{
			try
			{
				job.join();
			}
			catch (InterruptedException e)
			{
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsEventHandler#getAnalyticsURL()
	 */
	public String getAnalyticsURL()
	{
		if (StringUtil.isEmpty(url))
		{
			return DEFAULT_URL;
		}
		return url;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsEventHandler#getTimeout()
	 */
	public int getTimeout()
	{
		return timeout;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsEventHandler#getLastResponseCode()
	 */
	public int getLastResponseCode()
	{
		return responseCode;
	}

	/**
	 * Sends a ping.
	 * 
	 * @param event
	 * @param user
	 * @return The ping response code.
	 */
	protected int sendPing(AnalyticsEvent event, IAnalyticsUser user)
	{
		HttpURLConnection connection = null;
		DataOutputStream output = null;

		try
		{
			URL url = new URL(getAnalyticsURL());
			connection = (HttpURLConnection) url.openConnection();
			if (user != null)
			{
				connection.setRequestProperty("Cookie", user.getCookie() + "; uid=" + user.getGUID()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			connection.setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent()); //$NON-NLS-1$
			connection.setDoOutput(true);
			connection.setReadTimeout(getTimeout());
			connection.setConnectTimeout(getTimeout());

			connection.setRequestMethod("POST"); //$NON-NLS-1$
			// writes POST
			output = new DataOutputStream(connection.getOutputStream());
			String data = event.getEventString();
			output.writeBytes(data);
			output.flush();

			if (IdeLog.isTraceEnabled(UsagePlugin.getDefault(), IDebugScopes.USAGE))
			{
				IdeLog.logTrace(UsagePlugin.getDefault(), MessageFormat.format("Sending usage: {0}, {1}", url, data)); //$NON-NLS-1$
			}

			int code = connection.getResponseCode();
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN)
			{
				UsagePlugin.logError(MessageFormat.format(Messages.StudioAnalytics_connection_unauthorized,
						Integer.toString(code)));
			}
			else if (code < HttpURLConnection.HTTP_OK || code > HttpURLConnection.HTTP_RESET)
			{
				UsagePlugin.logError(MessageFormat.format(Messages.StudioAnalytics_connection_failed,
						Integer.toString(code)));
			}

			return code;
		}
		catch (Exception e)
		{
			UsagePlugin.logError(e);
			return HttpURLConnection.HTTP_UNAVAILABLE;
		}
		finally
		{
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException ignore)
				{
				}
			}
			if (connection != null)
			{
				connection.disconnect();
			}
		}
	}

	/**
	 * Returns <code>true</code> if the response code is under 500, or above 509.
	 * 
	 * @param code
	 * @return <code>true</code> if the response code is within the valid range
	 */
	protected static boolean isValidResponse(int code)
	{
		return code < 500 || code >= 510;
	}

	/**
	 * An analytics events comparator.
	 */
	protected static class AnalyticsEventComparator implements Comparator<AnalyticsEvent>
	{

		private static String PROJECT_CREATE = "project.create"; //$NON-NLS-1$
		private static String PROJECT_DELETE = "project.delete"; //$NON-NLS-1$

		public int compare(AnalyticsEvent o1, AnalyticsEvent o2)
		{
			return calculatePriority(o1) - calculatePriority(o2);
		}

		private int calculatePriority(AnalyticsEvent event)
		{
			String eventName = event.getEventName();

			if (eventName.startsWith(PROJECT_CREATE))
			{
				return -1;
			}
			if (eventName.startsWith(PROJECT_DELETE))
			{
				return 1;
			}
			return 0;
		}
	}
}
