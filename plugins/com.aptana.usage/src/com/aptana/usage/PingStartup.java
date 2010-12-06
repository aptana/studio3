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
 * with certain Eclipse Public Licensed code and certain additional terms
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
package com.aptana.usage;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.ui.IStartup;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.usage.preferences.IPreferenceConstants;
import com.eaio.uuid.MACAddress;

public class PingStartup implements IStartup
{

	private static final String ENCODING = "UTF-8"; //$NON-NLS-1$
	private static final String UPDATE_URL = "https://ping.aptana.com/ping.php"; //$NON-NLS-1$
	private static final int READ_TIMEOUT = 15 * 60 * 1000; // 15 minutes in milliseconds
	private static final long TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

	private static String osName;
	private static String osVersion;

	public void earlyStartup()
	{
		if (Platform.inDevelopmentMode())
		{
			return;
		}

		Job job = new Job("Sending Ping...") //$NON-NLS-1$
		{
			@Override
			protected IStatus run(IProgressMonitor monitor)
			{
				updateAnonymousId();
				schedule(TWENTY_FOUR_HOURS);
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.setPriority(Job.BUILD);
		job.schedule();
	}

	/**
	 * Updates the IDE ID if it hasn't been set. This ID is used just to track updates; no personal information is
	 * shared or retained.
	 */
	private static void updateAnonymousId()
	{
		String queryString = null;
		List<String> keyValues = new ArrayList<String>();

		// builds the key/value pairs
		if (!Platform.getPreferencesService().getBoolean(UsagePlugin.PLUGIN_ID, IPreferenceConstants.P_IDE_HAS_RUN,
				false, new IScopeContext[] { new ConfigurationScope() }))
		{
			EventLogger.getInstance().logEvent("first_run"); //$NON-NLS-1$
		}
		add(keyValues, "id", getApplicationId()); //$NON-NLS-1$
		add(keyValues, "version", UsagePlugin.getPluginVersion()); //$NON-NLS-1$
		add(keyValues, "product", System.getProperty("eclipse.product")); //$NON-NLS-1$ //$NON-NLS-2$
		add(keyValues, "eclipse_version", System.getProperty("osgi.framework.version")); //$NON-NLS-1$ //$NON-NLS-2$
		add(keyValues, "os_architecture", System.getProperty("os.arch")); //$NON-NLS-1$ //$NON-NLS-2$
		parseOSNameAndVersion();
		add(keyValues, "os_name", osName); //$NON-NLS-1$
		add(keyValues, "os_version", osVersion); //$NON-NLS-1$
		add(keyValues, LogEventTypes.STUDIO_KEY, MACAddress.getMACAddress());

		// adds date/time stamp
		EventLogger.getInstance().logEvent(LogEventTypes.DATE_TIME);

		// adds any custom key/value pairs
		EventInfo[] events = EventLogger.getInstance().getEvents();
		for (EventInfo event : events)
		{
			// ignores preview events
			if (!event.getEventType().equals(LogEventTypes.PREVIEW))
			{
				// specifies the key as an array in case we have more than one value of the same event type
				String key = event.getEventType() + "[]"; //$NON-NLS-1$
				// combines the date/time stamp with the value
				String value = Long.toString(event.getDateTime()) + ":" + event.getMessage(); //$NON-NLS-1$
				add(keyValues, key, value);
			}
		}

		// creates POST query string
		queryString = join("&", keyValues.toArray(new String[keyValues.size()])); //$NON-NLS-1$

		// sends ping and clear log events based on the result
		if (sendUpdate(queryString))
		{
			EventLogger.getInstance().clearEvents();
		}
		else
		{
			// removes anything older than 28 days
			long fourWeeksAgo = addDayInterval(System.currentTimeMillis(), -28);
			EventLogger.getInstance().clearEvents(fourWeeksAgo);
		}
	}

	public static String getApplicationId()
	{
		String id = Platform.getPreferencesService().getString(UsagePlugin.PLUGIN_ID, IPreferenceConstants.P_IDE_ID,
				null, null);
		if (id == null)
		{
			id = UUID.randomUUID().toString();
			// saves the id in configuration scope so it's shared by all workspaces
			IEclipsePreferences prefs = (new ConfigurationScope()).getNode(UsagePlugin.PLUGIN_ID);
			prefs.put(IPreferenceConstants.P_IDE_ID, id);
			prefs.putBoolean(IPreferenceConstants.P_IDE_HAS_RUN, true);
			try
			{
				prefs.flush();
			}
			catch (BackingStoreException e)
			{
				// ignores
			}
		}
		return id;
	}

	private static void parseOSNameAndVersion()
	{
		osName = System.getProperty("os.name"); //$NON-NLS-1$
		osVersion = System.getProperty("os.version"); //$NON-NLS-1$
		if (osName.equals("Linux")) //$NON-NLS-1$
		{
			// tries to get more precise information for Linux OSes
			try
			{
				File f;
				if ((f = new File("/etc/lsb-release")).canRead()) //$NON-NLS-1$
				{
					// Debian-based distribution; the file has same syntax as Java properties
					Properties props = new Properties();
					props.load(new FileInputStream(f));
					osName = props.getProperty("DISTRIB_ID"); //$NON-NLS-1$
					osVersion = props.getProperty("DISTRIB_RELEASE"); //$NON-NLS-1$
				}
				else if ((f = new File("/etc/redhat-release")).canRead()) //$NON-NLS-1$
				{
					// RedHat-based distribution
					BufferedReader in = null;
					try
					{
						in = new BufferedReader(new FileReader(f));
						String line = in.readLine();
						int index = line.indexOf(" release"); //$NON-NLS-1$
						if (index >= 0)
						{
							osName = line.substring(0, index);
						}
					}
					catch (Exception e)
					{
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								// ignores
							}
						}
					}
					osVersion = getVersion(f);
				}
				else if ((f = new File("/etc/SuSE-release")).canRead()) //$NON-NLS-1$
				{
					// SuSE-based distribution
					BufferedReader in = null;
					try
					{
						in = new BufferedReader(new FileReader(f));
						osName = in.readLine();
					}
					catch (Exception e)
					{
					}
					finally
					{
						if (in != null)
						{
							try
							{
								in.close();
							}
							catch (IOException e)
							{
								// ignores
							}
						}
					}
					osVersion = getVersion(f);
				}
			}
			catch (IOException e)
			{
			}
		}
	}

	private static String getVersion(File f)
	{
		try
		{
			Scanner sc = new Scanner(f);
			return sc.findInLine("(\\d)+((\\.)(\\d)+)*"); //$NON-NLS-1$
		}
		catch (Exception e)
		{
		}
		return null;
	}

	private static void add(List<String> list, String key, String value)
	{
		String encoded = urlEncodeKeyValuePair(key, value);
		if (encoded != null)
		{
			list.add(encoded);
		}
	}

	private static String urlEncodeKeyValuePair(String key, String value)
	{
		if (key == null || value == null)
		{
			return null;
		}
		try
		{
			StringBuilder text = new StringBuilder();
			text.append(URLEncoder.encode(key, ENCODING)).append("=").append(URLEncoder.encode(value, ENCODING)); //$NON-NLS-1$
			return text.toString();
		}
		catch (UnsupportedEncodingException e)
		{
		}
		return null;
	}

	private static String join(String delimiter, String[] items)
	{
		int length = items.length;
		if (length == 0)
		{
			return ""; //$NON-NLS-1$
		}

		StringBuilder text = new StringBuilder();
		for (int i = 0; i < length - 1; ++i)
		{
			text.append(items[i]).append(delimiter);
		}
		text.append(items[length - 1]);

		return text.toString();
	}

	private static long addDayInterval(long millis, int dayInterval)
	{
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT")); //$NON-NLS-1$
		calendar.setTimeInMillis(millis);
		calendar.add(Calendar.DAY_OF_MONTH, dayInterval);
		return calendar.getTimeInMillis();
	}

	private static String getUserAgent()
	{
		return "Aptana/3.0"; //$NON-NLS-1$
	}

	private static boolean sendUpdate(String queryString)
	{
		URL url = null;
		GZIPOutputStream gos = null;
		DataOutputStream output = null;
		BufferedReader input = null;
		try
		{
			// gzips POST string
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(baos);
			gos.write(queryString.getBytes());
			gos.flush();
			gos.finish();

			byte[] gzippedData = baos.toByteArray();

			// creates the URL
			url = new URL(UPDATE_URL);

			// opens the connection and configure it
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Encoding", "gzip"); //$NON-NLS-1$ //$NON-NLS-2$
			connection.setRequestProperty("Content-Length", String.valueOf(gzippedData.length)); //$NON-NLS-1$
			connection.setRequestProperty("User-Agent", getUserAgent()); //$NON-NLS-1$
			connection.setReadTimeout(READ_TIMEOUT); // 15 second read timeout

			// writes POST
			output = new DataOutputStream(connection.getOutputStream());
			output.write(gzippedData);
			output.flush();
			
			// Get the response
			input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuffer sb = new StringBuffer();
			String line;

			while ((line = input.readLine()) != null)
			{
				sb.append(line);
			}
			
			// NOTE: Leave sysout here so we can use this during debugging
			//System.out.println(sb.toString());

			return true;
		}
		catch (UnknownHostException e)
		{
			// happens when user is offline or we could not resolve aptana.com
		}
		catch (MalformedURLException e)
		{
			if (url != null)
			{
				UsagePlugin.logError(MessageFormat.format(Messages.PingStartup_ERR_MalformedURL, url), e);
			}
		}
		catch (IOException e)
		{
			UsagePlugin.logError(Messages.PingStartup_ERR_IOException, e);
		}
		catch (Exception e)
		{
			UsagePlugin.logError(Messages.PingStartup_ERR_FailedToContactServer, e);
		}
		finally
		{
			if (gos != null)
			{
				try
				{
					gos.close();
				}
				catch (IOException e)
				{
					// ignores
				}
			}
			if (output != null)
			{
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					// ignores
				}
			}
			if (input != null)
			{
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
		return false;
	}
}
