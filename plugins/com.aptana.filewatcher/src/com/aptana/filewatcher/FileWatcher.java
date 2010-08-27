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
