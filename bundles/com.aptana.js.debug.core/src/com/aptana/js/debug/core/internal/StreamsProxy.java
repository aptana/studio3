/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.debug.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;

import com.aptana.core.util.IOUtil;
import com.aptana.debug.core.IDebugCoreConstants;
import com.aptana.debug.core.IExtendedStreamsProxy;

/**
 * @author Max Stepanov
 */
public class StreamsProxy implements IExtendedStreamsProxy
{

	private final Map<String, OutputStreamMonitor> monitorMap = new HashMap<String, OutputStreamMonitor>();

	/**
	 * StreamsProxy
	 * 
	 * @param outIn
	 * @param errIn
	 */
	public StreamsProxy(InputStream outIn, InputStream errIn, IProcess process)
	{
		String encoding = getEncoding(process);
		monitorMap.put(IDebugCoreConstants.ID_STANDARD_OUTPUT_STREAM, createStreamMonitor(outIn, encoding, process));
		monitorMap.put(IDebugCoreConstants.ID_STANDARD_ERROR_STREAM, createStreamMonitor(errIn, encoding, process));
		for (OutputStreamMonitor monitor : monitorMap.values())
		{
			monitor.startMonitoring();
		}
	}

	public StreamsProxy(Map<String, InputStream> streams, IProcess process)
	{
		String encoding = getEncoding(process);
		for (Map.Entry<String, InputStream> entry : streams.entrySet())
		{
			monitorMap.put(entry.getKey(), createStreamMonitor(entry.getValue(), encoding, process));
		}
		for (OutputStreamMonitor monitor : monitorMap.values())
		{
			monitor.startMonitoring();
		}
	}

	private String getEncoding(IProcess process)
	{
		String encoding = process.getLaunch().getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);
		if (encoding == null)
		{
			encoding = IOUtil.UTF_8;
		}
		return encoding;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.debug.core.IExtendedStreamsProxy#getStreamMonitor(java.lang .String)
	 */
	public IStreamMonitor getStreamMonitor(String streamIdentifier)
	{
		return monitorMap.get(streamIdentifier);
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.debug.core.IExtendedStreamsProxy#getStreamIdentifers()
	 */
	public String[] getStreamIdentifers()
	{
		return monitorMap.keySet().toArray(new String[monitorMap.size()]);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getErrorStreamMonitor()
	 */
	public IStreamMonitor getErrorStreamMonitor()
	{
		return getStreamMonitor(IDebugCoreConstants.ID_STANDARD_ERROR_STREAM);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#getOutputStreamMonitor()
	 */
	public IStreamMonitor getOutputStreamMonitor()
	{
		return getStreamMonitor(IDebugCoreConstants.ID_STANDARD_OUTPUT_STREAM);
	}

	/*
	 * @see org.eclipse.debug.core.model.IStreamsProxy#write(java.lang.String)
	 */
	public void write(String input) throws IOException
	{
		throw new IOException("not supported"); //$NON-NLS-1$
	}

	/**
	 * Instantiates and returns an {@link OutputStreamMonitor}.
	 * 
	 * @param stream
	 * @param encoding
	 * @param process
	 * @return an {@link OutputStreamMonitor}
	 */
	protected OutputStreamMonitor createStreamMonitor(InputStream stream, String encoding, IProcess process)
	{
		return new OutputStreamMonitor(stream, encoding);
	}

}
