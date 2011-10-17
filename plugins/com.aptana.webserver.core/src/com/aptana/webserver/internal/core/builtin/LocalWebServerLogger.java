/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.webserver.internal.core.builtin;

import java.io.IOException;

import org.apache.http.HttpException;
import org.apache.http.nio.NHttpConnection;
import org.apache.http.nio.protocol.EventListener;

import com.aptana.core.logging.IdeLog;
import com.aptana.webserver.core.WebServerCorePlugin;

/**
 * @author Max Stepanov
 */
/* package */class LocalWebServerLogger implements EventListener
{

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#fatalIOException(java.io.IOException,
	 * org.apache.http.nio.NHttpConnection)
	 */
	public void fatalIOException(IOException ex, NHttpConnection conn)
	{
		IdeLog.logWarning(WebServerCorePlugin.getDefault(), ex);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#fatalProtocolException(org.apache.http.HttpException,
	 * org.apache.http.nio.NHttpConnection)
	 */
	public void fatalProtocolException(HttpException ex, NHttpConnection conn)
	{
		IdeLog.logWarning(WebServerCorePlugin.getDefault(), ex);
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionOpen(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionOpen(NHttpConnection conn)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionClosed(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionClosed(NHttpConnection conn)
	{
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.http.nio.protocol.EventListener#connectionTimeout(org.apache.http.nio.NHttpConnection)
	 */
	public void connectionTimeout(NHttpConnection conn)
	{
	}

}
