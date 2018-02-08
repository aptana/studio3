/**
 * Aptana Studio
 * Copyright (c) 2005-2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.usage.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IOUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.usage.AnalyticsEvent;
import com.aptana.usage.IAnalyticsLogger;
import com.aptana.usage.UsagePlugin;

public class AnalyticsLogger implements IAnalyticsLogger
{

	/**
	 * The path on disk where events should be persisted.
	 */
	private final IPath directory;

	public AnalyticsLogger(IPath directory)
	{
		this.directory = directory;
		if (!this.directory.toFile().isDirectory())
		{
			this.directory.toFile().mkdirs();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsLogger#logEvent(com.aptana.usage.AnalyticsEvent)
	 */
	public synchronized void logEvent(AnalyticsEvent event)
	{
		// persist the event to disk
		FileWriter writer = null;
		try
		{
			writer = new FileWriter(getFile(event));
			writer.write(event.toJSON());
		}
		catch (IOException e)
		{
			IdeLog.logError(UsagePlugin.getDefault(),
					MessageFormat.format("Unable to persist analytics event to disk! Event: {0}", event.toJSON()));
		}
		finally
		{
			if (writer != null)
			{
				try
				{
					writer.close();
				}
				catch (IOException e)
				{
					// ignore
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsLogger#clearEvents()
	 */
	public synchronized void clearEvents()
	{
		// Erase all events from disk
		File[] files = this.directory.toFile().listFiles();
		for (File file : files)
		{
			if (!file.delete())
			{
				file.deleteOnExit();
			}
		}
	}

	private File getFile(AnalyticsEvent event)
	{
		return this.directory.append(event.hashCode() + ".json").toFile();
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsLogger#clearEvent(com.aptana.usage.AnalyticsEvent)
	 */
	public synchronized void clearEvent(AnalyticsEvent event)
	{
		// erase specific event from disk
		File file = getFile(event);
		if (file.isFile())
		{
			if (!file.delete())
			{
				file.deleteOnExit();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.usage.IAnalyticsLogger#getEvents()
	 */
	public synchronized List<AnalyticsEvent> getEvents()
	{
		// Load all persisted events from disk
		File[] files = this.directory.toFile().listFiles();
		if (files == null || files.length == 0)
		{
			return Collections.emptyList();
		}
		ArrayList<AnalyticsEvent> events = new ArrayList<AnalyticsEvent>(files.length);
		for (File file : files)
		{
			InputStream stream = null;
			try
			{
				stream = new FileInputStream(file);
				String json = IOUtil.read(stream);
				if (!StringUtil.isEmpty(json))
				{
					events.add(AnalyticsEvent.fromJSON(json));
				}
			}
			catch (FileNotFoundException e)
			{
				// Should never happen...
				IdeLog.logWarning(UsagePlugin.getDefault(), e);
			}
			finally
			{
				if (stream != null)
				{
					try
					{
						stream.close();
					}
					catch (IOException e)
					{
						// ignore
					}
				}
			}
		}
		events.trimToSize();
		return events;
	}
}
