/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
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
