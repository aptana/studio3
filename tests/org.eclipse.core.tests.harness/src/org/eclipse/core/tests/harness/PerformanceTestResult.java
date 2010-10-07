/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.harness;

import java.io.PrintWriter;
import java.util.*;
import junit.framework.*;

/**
 * Test result for a performance test.  Keeps track of all timers that
 * have been created within the test. 
 */
public class PerformanceTestResult extends TestResult {
	protected PrintWriter output;
	protected ArrayList timerList = new ArrayList();
	protected HashMap timers = new HashMap();

	public PerformanceTestResult() {
		this(new PrintWriter(System.out));
	}

	public PerformanceTestResult(PrintWriter outputStream) {
		this.output = outputStream;
	}

	/**
	 * Informs the result that a test was completed.
	 */
	public synchronized void endTest(Test test) {
		print();
	}

	/**
	 * Prints the test result
	 */
	public synchronized void print() {
		stopTimers();
		printHeader(output);
		printErrors(output);
		printFailures(output);
		printTimings(output);
	}

	/**
	 * Prints the errors to the output
	 */
	protected void printErrors(PrintWriter out) {
		int count = errorCount();
		if (count != 0) {
			if (count == 1)
				out.println("There was " + count + " error:");
			else
				out.println("There were " + count + " errors:");
			int i = 1;
			for (Enumeration e = errors(); e.hasMoreElements(); i++) {
				TestFailure failure = (TestFailure) e.nextElement();
				out.println(i + ") " + failure.failedTest());
				failure.thrownException().printStackTrace(out);
			}
		}
	}

	/**
	 * Prints the failures to the output
	 */
	protected void printFailures(PrintWriter out) {
		int count = failureCount();
		if (count != 0) {
			if (count == 1)
				out.println("There was " + count + " failure:");
			else
				out.println("There were " + count + " failures:");
			int i = 1;
			for (Enumeration e = failures(); e.hasMoreElements(); i++) {
				TestFailure failure = (TestFailure) e.nextElement();
				out.println(i + ") " + failure.failedTest());
				failure.thrownException().printStackTrace(out);
			}
		}
	}

	/**
	 * Prints the header of the report
	 */
	protected void printHeader(PrintWriter out) {
		if (wasSuccessful()) {
			out.println();
			out.print("OK");
			out.println(" (" + runCount() + " tests)");
		} else {
			out.println();
			out.println("!!!FAILURES!!!");
			out.println("Test Results:");
			out.println("Run: " + runCount() + " Failures: " + failureCount() + " Errors: " + errorCount());
		}
	}

	/**
	 * Prints the timings of the result.
	 */
	protected void printTimings(PrintWriter out) {
		// print out all timing results to the console
		for (Iterator it = timerList.iterator(); it.hasNext();) {
			PerformanceTimer timer = (PerformanceTimer) it.next();
			out.println("Timing " + timer.getName() + " : " + timer.getElapsedTime() + " ms ");
		}
	}

	/**
	 * Start the test
	 */
	public synchronized void startTest(Test test) {
		super.startTest(test);
		System.out.print(".");
	}

	/**
	 * Start the timer with the given name.  If the timer has already
	 * been created, send it a startTiming message.  If not, create it
	 * and send the new timer the startTiming message.
	 */

	public synchronized void startTimer(String timerName) {
		PerformanceTimer timer = (PerformanceTimer) timers.get(timerName);
		if (timer == null) {
			timer = new PerformanceTimer(timerName);
			timers.put(timerName, timer);
			timerList.add(timer);
		}
		timer.startTiming();
	}

	/**
	 * Look up the timer with the given name and send it a stopTiming
	 * message.  If the timer does not exist, report an error.
	 */
	public synchronized void stopTimer(String timerName) {
		PerformanceTimer timer = (PerformanceTimer) timers.get(timerName);
		if (timer == null) {
			throw new Error(timerName + " is not a valid timer name ");
		}
		timer.stopTiming();
	}

	/**
	 * Stops all timers
	 */
	protected void stopTimers() {
		for (Iterator it = timerList.iterator(); it.hasNext();) {
			((PerformanceTimer) it.next()).stopTiming();
		}
	}
}
