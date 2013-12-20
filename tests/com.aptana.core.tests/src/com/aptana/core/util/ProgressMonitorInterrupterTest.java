/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.aptana.core.util.ProgressMonitorInterrupter.InterruptDelegate;

/**
 * @author Max Stepanov
 */
@SuppressWarnings("nls")
public class ProgressMonitorInterrupterTest {

	@Test
	public void testInterruptJob() throws InterruptedException {
		final boolean[] passed = new boolean[] { false };
		Job job = new Job("Test") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(monitor);
				long start = System.currentTimeMillis();
				try {
					Object lock = new Object();
					synchronized (lock) {
						lock.wait(10000);
					}
				}
				catch (InterruptedException e) {
					passed[0] = (System.currentTimeMillis() - start) < 2000;
				}
				finally {
					interrupter.dispose();
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule();
		Thread.sleep(500);
		job.cancel();
		job.join();
		assertTrue(passed[0]);
	}

	@Test
	public void testInterruptJobWithDelegate() throws InterruptedException {
		final boolean[] passed = new boolean[] { false };
		Job job = new Job("Test") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(monitor);
				long start = System.currentTimeMillis();
				try {
					final Object lock = new Object();
					ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(new InterruptDelegate() {
						public void interrupt() {
							synchronized (lock) {
								lock.notifyAll();
							}
						}
					});
					synchronized (lock) {
						lock.wait(10000);
					}
				}
				catch (InterruptedException e) {
				}
				finally {
					passed[0] = (System.currentTimeMillis() - start) < 2000;
					ProgressMonitorInterrupter.setCurrentThreadInterruptDelegate(null);
					interrupter.dispose();
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule();
		Thread.sleep(500);
		job.cancel();
		job.join();
		assertTrue(passed[0]);
	}

	@Test
	public void testEarlyDispose() throws InterruptedException {
		final boolean[] passed = new boolean[] { false };
		Job job = new Job("Test") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(monitor);
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {
				}
				interrupter.dispose();
				final Object lock = new Object();
				synchronized (lock) {
					try {
						lock.wait(2000);
						passed[0] = true;
					}
					catch (InterruptedException e) {
					}
				}
				return Status.OK_STATUS;
			}

		};
		job.schedule();
		Thread.sleep(500);
		job.cancel();
		job.join();
		assertTrue(passed[0]);
	}

	@Test
	public void testNull() {
		ProgressMonitorInterrupter interrupter = new ProgressMonitorInterrupter(null);
		interrupter.dispose();
	}

}
