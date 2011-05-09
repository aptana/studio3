/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Max Stepanov
 *
 */
public class ProgressMonitorInterrupter {

	private static final int CHECK_INTERVAL = 1000; /* 1s */
	
	private final IProgressMonitor monitor;
	private final Thread thread;
	private final MonitorThread monitorThread;
	
	/**
	 * 
	 */
	public ProgressMonitorInterrupter(IProgressMonitor monitor) {
		this.monitor = monitor;
		this.thread = Thread.currentThread();
		monitorThread = new MonitorThread();
		monitorThread.start();
	}
	
	public void dispose() {
		monitorThread.interrupt();
	}
		
	private class MonitorThread extends Thread {

		public MonitorThread() {
			super("Progress Monitor Thread"); //$NON-NLS-1$
			setDaemon(true);
		}

		@Override
		public void run() {
			try {
				while (!interrupted()) {
					if (monitor.isCanceled()) {
						thread.interrupt();
						break;
					}
					sleep(CHECK_INTERVAL);
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
	}

}
