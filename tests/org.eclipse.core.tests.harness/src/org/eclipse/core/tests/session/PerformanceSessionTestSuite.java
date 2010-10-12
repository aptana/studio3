/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.session;

import junit.framework.*;
import org.eclipse.core.tests.session.SetupManager.SetupException;

/**
 * Runs perfomance test cases multiple times (if they don't fail), 
 * enabling assertions for the first run.
 */
public class PerformanceSessionTestSuite extends SessionTestSuite {

	/**
	 * This custom test result allows multiple test runs to show up as a 
	 * single run. 
	 */
	private static class ConsolidatedTestResult extends TestResult {
		private boolean failed;
		private int runs = 0;
		private boolean started = false;
		private TestResult target;
		private int timesToRun;

		public ConsolidatedTestResult(TestResult target, int timesToRun) {
			this.target = target;
			this.timesToRun = timesToRun;
		}

		public synchronized void addError(Test test, Throwable t) {
			failed = true;
			target.addError(test, t);
		}

		public synchronized void addFailure(Test test, AssertionFailedError t) {
			failed = true;
			target.addFailure(test, t);
		}

		public void endTest(Test test) {
			runs++;
			if (!failed && runs < timesToRun)
				return;
			target.endTest(test);
		}

		public synchronized boolean shouldStop() {
			if (failed)
				return true;
			return target.shouldStop();
		}

		public void startTest(Test test) {
			// should not try to start again ater failing once
			if (failed)
				throw new IllegalStateException();
			if (started)
				return;
			started = true;
			target.startTest(test);
		}
	}

	public static final String PROP_PERFORMANCE = "perf_ctrl";

	private int timesToRun;

	public PerformanceSessionTestSuite(String pluginId, int timesToRun) {
		super(pluginId);
		this.timesToRun = timesToRun;
	}

	public PerformanceSessionTestSuite(String pluginId, int timesToRun, Class theClass) {
		super(pluginId, theClass);
		this.timesToRun = timesToRun;
	}

	public PerformanceSessionTestSuite(String pluginId, int timesToRun, Class theClass, String name) {
		super(pluginId, theClass, name);
		this.timesToRun = timesToRun;
	}

	public PerformanceSessionTestSuite(String pluginId, int timesToRun, String name) {
		super(pluginId, name);
		this.timesToRun = timesToRun;
	}

	protected void runSessionTest(TestDescriptor descriptor, TestResult result) {
		try {
			fillTestDescriptor(descriptor);
		} catch (SetupException e) {
			Throwable cause = e.getCause() == null ? e : e.getCause();
			result.addError(descriptor.getTest(), cause);
			return;
		}
		descriptor.getSetup().setSystemProperty("eclipse.perf.dbloc", System.getProperty("eclipse.perf.dbloc"));
		descriptor.getSetup().setSystemProperty("eclipse.perf.config", System.getProperty("eclipse.perf.config"));
		// run test cases n-1 times
		ConsolidatedTestResult consolidated = new ConsolidatedTestResult(result, timesToRun);
		for (int i = 0; !consolidated.shouldStop() && i < timesToRun - 1; i++)
			descriptor.run(consolidated);
		if (consolidated.shouldStop())
			return;
		// for the n-th run, enable assertions
		descriptor.getSetup().setSystemProperty("eclipse.perf.assertAgainst", System.getProperty("eclipse.perf.assertAgainst"));
		descriptor.run(consolidated);
	}
}
