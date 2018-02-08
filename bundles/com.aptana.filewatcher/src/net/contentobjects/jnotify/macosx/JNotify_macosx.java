/*******************************************************************************
 * JNotify - Allow java applications to register to File system events.
 * 
 * Copyright (C) 2005 - Content Objects
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 ******************************************************************************
 *
 * Content Objects, Inc., hereby disclaims all copyright interest in the
 * library `JNotify' (a Java library for file system events). 
 * 
 * Yahali Sherman, 21 November 2005
 *    Content Objects, VP R&D.
 *    
 ******************************************************************************
 * Author : Omry Yadan
 ******************************************************************************/
package net.contentobjects.jnotify.macosx;

import net.contentobjects.jnotify.JNotifyException;

@SuppressWarnings("nls")
public class JNotify_macosx
{
	private static Object initCondition = new Object();
	private static Object countLock = new Object();
	private static int watches = 0;

	static
	{
		System.loadLibrary("jnotify"); //$NON-NLS-1$
		Thread thread = new Thread("FSEvent thread") //$NON-NLS-1$
		{
			public void run()
			{
				nativeInit();
				synchronized (initCondition)
				{
					initCondition.notifyAll();
					initCondition = null;
				}
				while (true)
				{
					synchronized (countLock)
					{
						while (watches == 0)
						{
							try
							{
								countLock.wait();
							}
							catch (InterruptedException e)
							{
							}
						}
					}
					nativeNotifyLoop();
				}
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	private static native void nativeInit();
	private static native int nativeAddWatch(String path) throws JNotifyException;
	private static native boolean nativeRemoveWatch(int wd);
	private static native void nativeNotifyLoop();

	private static FSEventListener _eventListener;

	public static int addWatch(String path) throws JNotifyException
	{
		Object myCondition = initCondition;
		if (myCondition != null)
		{
			synchronized (myCondition)
			{
				while (initCondition != null)
				{
					try
					{
						initCondition.wait();
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}
		int wd = nativeAddWatch(path);
		synchronized (countLock)
		{
			watches++;
			countLock.notifyAll();
		}
		return wd;
	}

	public static boolean removeWatch(int wd)
	{
		boolean removed = nativeRemoveWatch(wd);
		if (removed)
		{
			synchronized (countLock)
			{
				watches--;
			}
		}
		return removed;
	}

	public static void callbackProcessEvent(int wd, String rootPath, String filePath, boolean recurse)
	{
		if (_eventListener != null)
		{
			_eventListener.notifyChange(wd, rootPath, filePath, recurse);
		}
	}

	public static void callbackInBatch(int wd, boolean state)
	{
		if (_eventListener != null)
		{
			if (state) {
				_eventListener.batchStart(wd);
			} else {
				_eventListener.batchEnd(wd);
			}
		}
	}

	public static void setNotifyListener(FSEventListener eventListener)
	{
		if (_eventListener == null)
		{
			_eventListener = eventListener;
		}
		else
		{
			throw new RuntimeException("Notify listener is already set. multiple notify listeners are not supported.");
		}
	}
}
