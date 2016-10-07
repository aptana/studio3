/*******************************************************************************
 * Copyright (c) 2000, 2015 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import junit.framework.AssertionFailedError;
import org.eclipse.core.runtime.jobs.Job;

/**
 * This class can be used for testing progress monitoring.
 * If you want to reuse one instance of this class for several
 * invocations, make sure that you call prepare() before every
 * invocation.
 * Call sanityCheck() after the operation whose progress monitoring
 * you are testing.
 */
public class FussyProgressMonitor extends TestProgressMonitor {
	public class FussyProgressAssertionFailed extends AssertionFailedError {
		/**
		 * All serializable objects should have a stable serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		FussyProgressAssertionFailed(String name) {
			super(name);
		}
	}

	private static final double EPS_FACTOR = 0.01;
	private static final long NOTICEABLE_DELAY = 1000; // milliseconds
	private boolean beginTaskCalled;
	private long beginTime;
	private boolean canceled;
	private int doneCalls;
	private boolean hasFailed;
	private boolean sanityCheckCalled = true;
	private String taskName;
	private int totalWork;
	private double workedSoFar;
	private Job job;
	public FussyProgressMonitor() {
		prepare();
	}
	/**
	 * Creates a fussy progress monitor that is associated with a particular job.
	 * On assertion failure the job name will be included in the failure message.
	 * @param job
	 */
	public FussyProgressMonitor(Job job) {
		this();
		this.job = job;
	}

	/**
	 *
	 * @param reason java.lang.String
	 * @param condition boolean
	 */
	private void assertTrue(String reason, boolean condition) {
		// silently ignore follow-up failures
		if (hasFailed) {
			return;
		}
		if (!condition) {
			hasFailed = true;
			if (job != null) {
				reason += " in job: " + job.getName();
			}
			throw new FussyProgressAssertionFailed(reason);
		}
		//Assert.assert(reason, condition);
	}

	/**
	 * Asserts that this progress monitor is all used up
	 */
	public void assertUsedUp() {
		assertTrue("beginTask has not been called on ProgressMonitor", beginTaskCalled);
		assertTrue("ProgressMonitor not used up", Math.round(workedSoFar) >= totalWork);
	}

	@Override
	public void beginTask(String name, int newTotalWork) {
		//if (beginTaskCalled && doneCalls > 0) {
		// this is a second call to beginTask which is allowed because
		// the previous task is done.
		//prepare();
		//}
		assertTrue("beginTask may only be called once (old name=" + taskName + ")", beginTaskCalled == false);
		beginTaskCalled = true;
		taskName = name;
		assertTrue("total work must be positive or UNKNOWN", newTotalWork > 0 || newTotalWork == UNKNOWN);
		this.totalWork = newTotalWork;
		beginTime = System.currentTimeMillis();
	}

	@Override
	public void done() {
		assertTrue("done must be called after beginTask", beginTaskCalled);
		assertTrue("done can only be called once", doneCalls == 0);
		//assert("done is called before all work is done", totalWork==UNKNOWN || totalWork==workedSoFar);
		workedSoFar = totalWork;
		doneCalls++;
	}

	@Override
	public void internalWorked(double work) {
		assertTrue("can accept calls to worked/internalWorked only after beginTask", beginTaskCalled);
		assertTrue("can accept calls to worked/internalWorked only before done is called", doneCalls == 0);
		assertTrue("amount worked should be positive, not " + work, work >= 0);
		if (work == 0) {
			CoreTest.debug("INFO: amount worked should be positive, not " + work);
		}
		workedSoFar += work;
		assertTrue("worked " + (workedSoFar - totalWork) + " more than totalWork", totalWork == UNKNOWN || workedSoFar <= totalWork + (totalWork * EPS_FACTOR));
	}

	@Override
	public boolean isCanceled() {
		return canceled;
	}

	/**
	 * should be called before every use of a FussyProgressMonitor
	 */
	public void prepare() {
		//if (!sanityCheckCalled)
		//EclipseWorkspaceTest.log("sanityCheck has not been called for previous use");
		sanityCheckCalled = false;
		taskName = null;
		totalWork = 0;
		workedSoFar = 0;
		beginTaskCalled = false;
		doneCalls = 0;
		hasFailed = false;
	}

	/**
	 *  should be called after every use of a FussyProgressMonitor
	 */
	public void sanityCheck() {
		if (sanityCheckCalled) {
			CoreTest.debug("sanityCheck has already been called");
		}
		sanityCheckCalled = true;
		//	EclipseWorkspaceTest.log("sanity checking: " + taskName + " : " + (System.currentTimeMillis() - beginTime) + " ms, " + workedSoFar);
		long duration = System.currentTimeMillis() - beginTime;
		if (duration > NOTICEABLE_DELAY && beginTaskCalled) {
			assertTrue("this operation took: " + duration + "ms, it should report progress", workedSoFar > 0);
		}
		assertTrue("done has not been called on ProgressMonitor", hasFailed || !beginTaskCalled || doneCalls > 0);
	}

	@Override
	public void setCanceled(boolean b) {
		boolean wasCanceled = isCanceled();
		canceled = b;
		if (canceled && !wasCanceled && job != null) {
			job.cancel();
		}
	}

	@Override
	public void setTaskName(String name) {
		taskName = name;
	}

	@Override
	public void subTask(String name) {
		// do nothing
	}

	@Override
	public void worked(int work) {
		internalWorked(work);
	}
}
