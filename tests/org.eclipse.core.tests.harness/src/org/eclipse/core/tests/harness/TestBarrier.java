/*******************************************************************************
 * Copyright (c) 2003, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import org.junit.Assert;

/**
 * This class acts as an implementation of a barrier that is appropriate for
 * concurrency test cases that want to fail if a thread fails to achieve a
 * particular state in a reasonable amount of time. This prevents test suites
 * from hanging indefinitely if a concurrency bug is found that would normally
 * result in an indefinite hang.
 */
public class TestBarrier {

	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_BLOCKED = 6;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_DONE = 5;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_RUNNING = 3;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_START = 1;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_WAIT_FOR_DONE = 4;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_WAIT_FOR_RUN = 2;
	/**
	 * Convenience status constant that can be interpreted differently by each
	 * test.
	 */
	public static final int STATUS_WAIT_FOR_START = 0;
	private final int myIndex;
	/**
	 * The status array and index for this barrier object
	 */
	private final int[] myStatus;

	/**
	 * Blocks the calling thread until the status integer at the given index
	 * is set to the given value. Fails if the status change does not occur in
	 * a reasonable amount of time.
	 * @param statuses the array of statuses that represent the states of
	 * an array of jobs or threads
	 * @param index the index into the statuses array that the calling
	 * thread is waiting for
	 * @param status the status that the calling thread should wait for
	 */
	private static void doWaitForStatus(int[] statuses, int index, int status, int timeout) {
		long start = System.currentTimeMillis();
		int i = 0;
		while (statuses[index] != status) {
			try {
				Thread.yield();
				Thread.sleep(100);
				Thread.yield();
			} catch (InterruptedException e) {
				//ignore
			}
			//sanity test to avoid hanging tests
			long elapsed = System.currentTimeMillis()-start;
			Assert.assertTrue("Timeout after " + elapsed + "ms waiting for status to change from " + getStatus(statuses[index]) + " to " + getStatus(status), i++ < timeout);
		}
	}

	private static String getStatus(int status) {
		switch (status) {
			case STATUS_WAIT_FOR_START :
				return "WAIT_FOR_START";
			case STATUS_START :
				return "START";
			case STATUS_WAIT_FOR_RUN :
				return "WAIT_FOR_RUN";
			case STATUS_RUNNING :
				return "RUNNING";
			case STATUS_WAIT_FOR_DONE :
				return "WAIT_FOR_DONE";
			case STATUS_DONE :
				return "DONE";
			case STATUS_BLOCKED :
				return "BLOCKED";
			default :
				return "UNKNOWN_STATUS";
		}
	}

	public static void waitForStatus(int[] location, int status) {
		doWaitForStatus(location, 0, status, 100);
	}

	/**
	 * Blocks the current thread until the given variable is set to the given
	 * value Times out after a predefined period to avoid hanging tests
	 */
	public static void waitForStatus(int[] location, int index, int status) {
		doWaitForStatus(location, index, status, 500);
	}

	/**
	 * Creates a new test barrier suitable for a single thread
	 */
	public TestBarrier() {
		this(new int[1], 0);
	}

	/**
	 * Creates a new test barrier suitable for a single thread, with the given initial status.
	 */
	public TestBarrier(int initalStatus) {
		this(new int[] {initalStatus}, 0);
	}

	/**
	 * Creates a new test barrier on the provided status array, suitable for
	 * acting as a barrier for multiple threads.
	 */
	public TestBarrier(int[] location, int index) {
		this.myStatus = location;
		this.myIndex = index;
	}

	/**
	 * Sets this barrier object's status.
	 */
	public void setStatus(int status) {
		myStatus[myIndex] = status;
	}

	/**
	 * Blocks the current thread until the receiver's status is set to the given
	 * value. Times out after a predefined period to avoid hanging tests
	 */
	public void waitForStatus(int status) {
		waitForStatus(myStatus, myIndex, status);
	}

	/**
	 * The same as other barrier methods, except it will not fail if the job
	 * does not start in a "reasonable" time. This is only appropriate for tests
	 * that are explicitly very long running.
	 */
	public void waitForStatusNoFail(int status) {
		doWaitForStatus(myStatus, myIndex, status, 100000);
	}
}
