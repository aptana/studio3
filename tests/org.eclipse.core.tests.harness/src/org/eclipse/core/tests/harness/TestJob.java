/*******************************************************************************
 * Copyright (c) 2003, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A job that runs for a specified amount of ticks, where each tick is represented
 * by a sleep for a specified amount of milliseconds.
 */
public class TestJob extends Job {
	private int ticks;
	private long tickLength;
	private int runCount = 0;

	/**
	 * A job that runs for one second in 100 millisecond increments.
	 */
	public TestJob(String name) {
		this(name, 10, 100);
	}

	/**
	 * A job that runs for the specified number of ticks at the
	 * given tick duration.
	 * @param name The name of this test job
	 * @param ticks The number of work ticks that this job should perform
	 * @param tickDuration The duration in milliseconds of each work tick
	 */
	public TestJob(String name, int ticks, long tickDuration) {
		super(name);
		this.ticks = ticks;
		this.tickLength = tickDuration;
	}

	/**
	 * Returns the number of times this job instance has been run, possibly including
	 * the current invocation if the job is currently running.
	 */
	public synchronized int getRunCount() {
		return runCount;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		setRunCount(getRunCount() + 1);
		//must have positive work
		monitor.beginTask(getName(), ticks <= 0 ? 1 : ticks);
		try {
			for (int i = 0; i < ticks; i++) {
				monitor.subTask("Tick: " + i);
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}
				try {
					Thread.sleep(tickLength);
				} catch (InterruptedException e) {
					//ignore
				}
				monitor.worked(1);
			}
			if (ticks <= 0) {
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	private synchronized void setRunCount(int count) {
		runCount = count;
	}
}
