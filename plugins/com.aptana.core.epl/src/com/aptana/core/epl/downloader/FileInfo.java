/*******************************************************************************
 * Copyright (c) 2009, Cloudsmith Inc.
 * The code, documentation and other materials contained herein have been
 * licensed under the Eclipse Public License - v 1.0 by the copyright holder
 * listed above, as the Initial Contributor under such license. The text or
 * such license is available at www.eclipse.org.
 ******************************************************************************/
package com.aptana.core.epl.downloader;

import java.util.Properties;

/**
 * Carries information about a file transfer.
 */
public class FileInfo
{
	public static final String PROPERTY_CONTENT_TYPE = "contentType"; //$NON-NLS-1$

	public static final String PROPERTY_LAST_MODIFIED = "lastModified"; //$NON-NLS-1$

	public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$

	public static final String PROPERTY_SIZE = "size"; //$NON-NLS-1$

	public static final String PROPERTY_SPEED = "speed"; //$NON-NLS-1$

	public static final long UNKNOWN_RATE = -1L;

	private String contentType;

	private long lastModified = 0L;

	private String name;

	private long size = -1L;

	private long averageSpeed = UNKNOWN_RATE;

	public FileInfo()
	{
		contentType = ""; //$NON-NLS-1$
		name = ""; //$NON-NLS-1$
	}

	public FileInfo(FileInfo fileInfo)
	{
		initFrom(fileInfo);
	}

	/*
	 * (non java doc) properties based method for possible use with resumable download
	 */
	public FileInfo(Properties properties)
	{
		name = properties.getProperty(PROPERTY_NAME);
		contentType = properties.getProperty(PROPERTY_CONTENT_TYPE);

		String v = properties.getProperty(PROPERTY_LAST_MODIFIED);
		if (v != null)
			lastModified = Long.parseLong(v);

		v = properties.getProperty(PROPERTY_SIZE);
		if (v != null)
			size = Long.parseLong(v);
		v = properties.getProperty(PROPERTY_SPEED);
		if (v != null)
			averageSpeed = Long.parseLong(v);
	}

	/*
	 * (non java doc) properties based method for possible use with resumable download
	 */
	public void addProperties(Properties properties)
	{
		if (contentType != null)
			properties.setProperty(PROPERTY_CONTENT_TYPE, contentType);
		if (lastModified != 0L)
			properties.setProperty(PROPERTY_LAST_MODIFIED, Long.toString(lastModified));
		if (name != null)
			properties.setProperty(PROPERTY_NAME, name);
		if (size != -1L)
			properties.setProperty(PROPERTY_SIZE, Long.toString(size));
		if (averageSpeed != UNKNOWN_RATE)
			properties.setProperty(PROPERTY_SPEED, Long.toString(averageSpeed));

	}

	public final String getContentType()
	{
		return contentType;
	}

	public long getLastModified()
	{
		return lastModified;
	}

	public final String getRemoteName()
	{
		return name;
	}

	public final long getSize()
	{
		return size;
	}

	public void initFrom(FileInfo info)
	{
		setName(info.getRemoteName());
		setContentType(info.getContentType());
		setSize(info.getSize());
		setLastModified(info.getLastModified());
	}

	public void reset()
	{
		name = null;
		contentType = null;
		size = -1;
		lastModified = 0;
	}

	public final void setContentType(String contentType)
	{
		this.contentType = contentType;
	}

	public void setLastModified(long timestamp)
	{
		lastModified = timestamp;
	}

	public final void setName(String name)
	{
		this.name = name;
	}

	public final void setSize(long size)
	{
		this.size = size;
	}

	/**
	 * Set the average transfer rate measured in bytes/second
	 * 
	 * @param averageSpeed
	 *            rate in bytes/second, or {@link #UNKNOWN_RATE}
	 */
	public void setAverageSpeed(long averageSpeed)
	{
		this.averageSpeed = averageSpeed;
	}

	/**
	 * Returns the average transfer rate measured in bytes/second.
	 * 
	 * @return transfer rate in bytes/second or {@link #UNKNOWN_RATE}
	 */
	public long getAverageSpeed()
	{
		return averageSpeed;
	}
}
