/**
 * Aptana Studio
 * Copyright (c) 2014 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.epl.downloader;

/**
 * Connection about the retry count and delay.
 * 
 * @author Praveen Innamuri
 */
public class ConnectionData
{

	private final int retryCount;
	private final long retryDelay;

	public ConnectionData(int retryCount, long retryDelay)
	{
		this.retryCount = retryCount;
		this.retryDelay = retryDelay;
	}

	public int getRetryCount()
	{
		return retryCount;
	}

	public long getRetryDelay()
	{
		return retryDelay;
	}
}
