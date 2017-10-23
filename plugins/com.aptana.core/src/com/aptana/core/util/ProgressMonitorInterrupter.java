/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * The ProgressMonitorInperrupter class allows handling progress interruptions in threads which use IO or
 * synchronization waits. Once it is set up, it monitors cancellation status of ProgressMonitor and sends interrupt to
 * the thread which allocated it. Some implementations which require custom interrupt procedures could assign
 * InterruptDelegates using setCurrentThreadInterruptDelegate and resetting back with null afterwards.
 * 
 * @author Max Stepanov
 */
public class ProgressMonitorInterrupter
{

	public interface InterruptDelegate
	{
		public void interrupt();
	}

	private static final int CHECK_INTERVAL = 1000; /* 1s */

	private final IProgressMonitor monitor;
	private final Thread thread;
	private final MonitorThread monitorThread;

	private static Map<Thread, InterruptDelegate> delegates = Collections
			.synchronizedMap(new WeakHashMap<Thread, ProgressMonitorInterrupter.InterruptDelegate>());

	/**
	 * 
	 */
	public ProgressMonitorInterrupter(IProgressMonitor monitor)
	{
		this.monitor = monitor != null ? monitor : new NullProgressMonitor();
		this.thread = Thread.currentThread();
		monitorThread = new MonitorThread();
		monitorThread.start();
	}

	public void dispose()
	{
		monitorThread.interrupt();
	}

	/**
	 * Sets interrupt delegate for the current thread
	 * 
	 * @param delegate
	 */
	public static void setCurrentThreadInterruptDelegate(InterruptDelegate delegate)
	{
		if (delegate != null)
		{
			delegates.put(Thread.currentThread(), delegate);
		}
		else
		{
			delegates.remove(Thread.currentThread());
		}
	}

	private static void interruptThread(Thread thread)
	{
		InterruptDelegate delegate = delegates.get(thread);
		if (delegate != null)
		{
			delegate.interrupt();
		}
		else
		{
			thread.interrupt();
		}
	}

	private class MonitorThread extends Thread
	{

		public MonitorThread()
		{
			super("Progress Monitor Thread"); //$NON-NLS-1$
			setDaemon(true);
		}

		@Override
		public void run()
		{
			try
			{
				while (!interrupted())
				{
					if (monitor.isCanceled())
					{
						interruptThread(thread);
						break;
					}
					sleep(CHECK_INTERVAL);
				}
			}
			catch (InterruptedException e)
			{
				Thread.currentThread().interrupt();
			}
		}
	}
}
