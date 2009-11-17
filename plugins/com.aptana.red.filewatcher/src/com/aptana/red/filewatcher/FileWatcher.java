package com.aptana.red.filewatcher;

import net.contentobjects.jnotify.IJNotify;
import net.contentobjects.jnotify.JNotifyException;
import net.contentobjects.jnotify.JNotifyListener;

import com.aptana.red.filewatcher.poller.PollingNotifier;

public class FileWatcher
{

	private static IJNotify _instance;

	private synchronized static IJNotify instance()
	{
		if (_instance == null)
		{
			String osName = System.getProperty("os.name").toLowerCase(); //$NON-NLS-1$
			if (osName.equals("linux")) //$NON-NLS-1$
			{
				try
				{
					_instance = (IJNotify) Class.forName("net.contentobjects.jnotify.linux.JNotifyAdapterLinux") //$NON-NLS-1$
							.newInstance();
				}
				catch (Exception e)
				{
					FileWatcherPlugin.log(e);
				}
			}
			else if (osName.startsWith("windows")) //$NON-NLS-1$
			{
				try
				{
					_instance = (IJNotify) Class.forName("net.contentobjects.jnotify.win32.JNotifyAdapterWin32") //$NON-NLS-1$
							.newInstance();
				}
				catch (Exception e)
				{
					FileWatcherPlugin.log(e);
				}
			}
			else if (osName.startsWith("mac os x")) //$NON-NLS-1$
			{
				try
				{
					_instance = (IJNotify) Class
							.forName("net.contentobjects.jnotify.macosx.JNotifyAdapterMacOSX").newInstance(); //$NON-NLS-1$
				}
				catch (Exception e)
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
