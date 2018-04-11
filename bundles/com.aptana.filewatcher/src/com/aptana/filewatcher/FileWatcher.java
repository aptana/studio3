/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filewatcher;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

import com.aptana.filewatcher.poller.PollingNotifier;

public class FileWatcher
{

	private static IJNotify _instance;
	private static boolean fgNotify = true;

	private synchronized static IJNotify instance()
	{
		if (_instance == null)
		{
			String className = null;
			if (Platform.OS_LINUX.equals(Platform.getOS()))
			{
				className = "net.contentobjects.jnotify.linux.JNotifyAdapterLinux"; //$NON-NLS-1$
			}
			else if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				className = "net.contentobjects.jnotify.win32.JNotifyAdapterWin32"; //$NON-NLS-1$
			}
			else if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				className = "net.contentobjects.jnotify.macosx.JNotifyAdapterMacOSX"; //$NON-NLS-1$
			}
			if (className != null)
			{
				try
				{
					Bundle b = FileWatcherPlugin.getDefault().getBundle();
					_instance = (IJNotify) b.loadClass(className).newInstance();
				}
				catch (Throwable e)
				{
					FileWatcherPlugin.log(e);
				}
			}
			if (_instance == null)
			{
				_instance = new PollingNotifier();
			}
		}
		return _instance;
	}

	public static int addWatch(String path, int mask, boolean watchSubtree, final JNotifyListener listener)
			throws JNotifyException
	{
		return addWatch(path, mask, watchSubtree, true, listener);
	}

	public static int addWatch(String path, int mask, boolean watchSubtree, boolean recursive,
			final JNotifyListener listener) throws JNotifyException
	{
		return instance().addWatch(path, mask, watchSubtree, recursive, listener);
	}

	public static boolean removeWatch(int watchId) throws JNotifyException
	{
		return instance().removeWatch(watchId);
	}

	public static synchronized void avoidNotify()
	{
		fgNotify = false;
	}

	public static synchronized void resumeNotify()
	{
		fgNotify = true;
	}

	public static synchronized boolean shouldNotify()
	{
		return fgNotify;
	}

}
