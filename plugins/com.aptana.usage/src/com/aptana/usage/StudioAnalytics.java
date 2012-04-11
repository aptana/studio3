/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage;

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
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.EclipseUtil;

public class StudioAnalytics
{

	private static final String ANALYTICS_URL;
	static
	{
		String url = EclipseUtil.getSystemProperty(IUsageSystemProperties.ANALYTICS_URL);
		ANALYTICS_URL = (url == null) ? "https://api.appcelerator.net/p/v1/app-track" : url; //$NON-NLS-1$
	}
	private static final int TIMEOUT = 5 * 1000; // 5 seconds

	private static StudioAnalytics instance;

	private int responseCode = 0;

	public synchronized static StudioAnalytics getInstance()
	{
		if (instance == null)
		{
			instance = new StudioAnalytics();
		}
		return instance;
	}

	public void sendEvent(final AnalyticsEvent event)
	{
		if (Platform.inDevelopmentMode() && !EclipseUtil.isTesting())
		{
			return;
		}

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
					List<AnalyticsEvent> events = AnalyticsLogger.getInstance().getEvents();
					// Sort the events. We want all project.create events to be first, and all project.delete events to
					// be last
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
				return Status.OK_STATUS;
			}
		};
		job.setSystem(!EclipseUtil.showSystemJobs());
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

	public int getLastResponseCode()
	{
		return responseCode;
	}

	private int sendPing(AnalyticsEvent event, IAnalyticsUser user)
	{
		HttpURLConnection connection = null;
		DataOutputStream output = null;

		try
		{
			URL url = new URL(ANALYTICS_URL);
			connection = (HttpURLConnection) url.openConnection();
			if (user != null)
			{
				connection.setRequestProperty("Cookie", user.getCookie()); //$NON-NLS-1$
			}
			connection.setRequestProperty("User-Agent", AnalyticsEvent.getUserAgent()); //$NON-NLS-1$
			connection.setDoOutput(true);
			connection.setReadTimeout(TIMEOUT);
			connection.setConnectTimeout(TIMEOUT);

			connection.setRequestMethod("POST"); //$NON-NLS-1$
			// writes POST
			output = new DataOutputStream(connection.getOutputStream());
			output.writeBytes(event.getEventString());
			output.flush();

			int code = connection.getResponseCode();
			if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == HttpURLConnection.HTTP_FORBIDDEN)
			{
				UsagePlugin.logError(MessageFormat.format(Messages.StudioAnalytics_connection_unauthorized,
						Integer.toString(code)));
			}
			else if (code < 200 || code > 205)
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

	private static boolean isValidResponse(int code)
	{
		return code < 500 || code >= 510;
	}

	private static class AnalyticsEventComparator implements Comparator<AnalyticsEvent>
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
