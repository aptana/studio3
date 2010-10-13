/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * If redistributing this code, this entire header must remain intact.
 */
package net.contentobjects.jnotify;

import java.io.IOException;

public abstract class JNotifyException extends IOException
{
	private static final long serialVersionUID = 1L;

	public final static int ERROR_UNSPECIFIED = 1;
	public final static int ERROR_WATCH_LIMIT_REACHED = 2;
	public final static int ERROR_PERMISSION_DENIED = 3;
	public final static int ERROR_NO_SUCH_FILE_OR_DIRECTORY = 4;
	
	
	protected final int _systemErrorCode;
	
	public JNotifyException(String s, int systemErrorCode)
	{
		super(s);
		_systemErrorCode = systemErrorCode;
	}
	
	public int getSystemError()
	{
		return _systemErrorCode;
	}
	
	public abstract int getErrorCode();
}
