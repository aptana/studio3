/**
 * Copyright (c) 2005-2010 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * If redistributing this code, this entire header must remain intact.
 */
package com.aptana.filewatcher;

import org.eclipse.core.runtime.Platform;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import com.aptana.filewatcher.poller.PollingNotifier;

public class FileWatcher
{

	private static IJNotify _instance;

	private synchronized static IJNotify instance()
	{
		if (_instance == null)
		{
			if (Platform.OS_LINUX.equals(Platform.getOS()))
			{
				try
				{
					_instance = (IJNotify) Class.forName("net.contentobjects.jnotify.linux.JNotifyAdapterLinux") //$NON-NLS-1$
							.newInstance();
				}
				catch (Throwable e)
				{
					FileWatcherPlugin.log(e);
				}
			}
			else if (Platform.OS_WIN32.equals(Platform.getOS()))
			{
				try
				{
					_instance = (IJNotify) Class.forName("net.contentobjects.jnotify.win32.JNotifyAdapterWin32") //$NON-NLS-1$
							.newInstance();
				}
				catch (Throwable e)
				{
					FileWatcherPlugin.log(e);
				}
			}
			else if (Platform.OS_MACOSX.equals(Platform.getOS()))
			{
				try
				{
					_instance = (IJNotify) Class
							.forName("net.contentobjects.jnotify.macosx.JNotifyAdapterMacOSX").newInstance(); //$NON-NLS-1$
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
		return instance().addWatch(path, mask, watchSubtree, listener);
	}

	public static boolean removeWatch(int watchId) throws JNotifyException
	{
		return instance().removeWatch(watchId);
	}

}
