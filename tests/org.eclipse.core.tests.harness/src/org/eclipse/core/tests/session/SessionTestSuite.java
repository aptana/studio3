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

import java.util.*;
import junit.framework.*;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.tests.session.SetupManager.SetupException;

public class SessionTestSuite extends TestSuite {
	public static final String CORE_TEST_APPLICATION = "org.eclipse.pde.junit.runtime.coretestapplication"; //$NON-NLS-1$	
	public static final String UI_TEST_APPLICATION = "org.eclipse.pde.junit.runtime.uitestapplication"; //$NON-NLS-1$	
	protected String applicationId = CORE_TEST_APPLICATION;
	private Set crashTests = new HashSet();
	private Set localTests = new HashSet();
	// the id for the plug-in whose classloader ought to be used to load the test case class
	protected String pluginId;
	private Setup setup;
	// true if test cases should run in the same (shared) session
	private boolean sharedSession;
	protected SessionTestRunner testRunner;

	public SessionTestSuite(String pluginId) {
		super();
		this.pluginId = pluginId;
	}

	public SessionTestSuite(String pluginId, Class theClass) {
		super(theClass);
		this.pluginId = pluginId;
	}

	public SessionTestSuite(String pluginId, Class theClass, String name) {
		super(theClass, name);
		this.pluginId = pluginId;
	}

	public SessionTestSuite(String pluginId, String name) {
		super(name);
		this.pluginId = pluginId;
	}

	/**
	 * Crash tests are not expected to complete (they fail if they do).
	 */
	public void addCrashTest(TestCase test) {
		crashTests.add(test);
		super.addTest(test);
	}

	/**
	 * Adds a local test, a test that is run locally, not in a separate session.
	 */
	public void addLocalTest(TestCase test) {
		localTests.add(test);
		super.addTest(test);
	}

	protected void fillTestDescriptor(TestDescriptor test) throws SetupException {
		if (test.getApplicationId() == null)
			test.setApplicationId(applicationId);
		if (test.getPluginId() == null)
			test.setPluginId(pluginId);
		if (test.getSetup() == null)
			test.setSetup(getSetup());
		if (!test.isCrashTest() && crashTests.contains(test.getTest()))
			test.setCrashTest(true);
		test.setTestRunner(getTestRunner());
	}

	public String getApplicationId() {
		return applicationId;
	}

	public Setup getSetup() throws SetupException {
		if (setup == null)
			setup = newSetup();
		return setup;
	}

	protected SessionTestRunner getTestRunner() {
		if (testRunner == null)
			testRunner = new SessionTestRunner();
		return testRunner;
	}

	protected Test[] getTests(boolean sort) {
		Test[] allTests = new Test[testCount()];
		Enumeration e = tests();
		for (int i = 0; i < allTests.length; i++)
			allTests[i] = (Test) e.nextElement();
		if (sort)
			Arrays.sort(allTests, new Comparator() {
				public int compare(Object o1, Object o2) {
					return ((TestCase) o1).getName().compareTo(((TestCase) o2).getName());
				}
			});
		return allTests;
	}

	private boolean isLocalTest(Test test) {
		return localTests.contains(test);
	}

	public boolean isSharedSession() {
		return sharedSession;
	}

	protected Setup newSetup() throws SetupException {
		Setup base =  SetupManager.getInstance().getDefaultSetup();
		base.setSystemProperty("org.eclipse.update.reconcile", "false"); //$NON-NLS-1$ //$NON-NLS-2$
		return base;
	}

	/**
	 * Runs this session test suite.  
	 */
	public void run(TestResult result) {
		if (!sharedSession) {
			super.run(result);
			return;
		}
		// running this session test suite in shared mode
		Enumeration tests = tests();
		Assert.isTrue(tests.hasMoreElements(), "A single test suite must be provided");
		Test onlyTest = (Test) tests.nextElement();
		Assert.isTrue(!tests.hasMoreElements(), "Only a single test suite can be run");
		Assert.isTrue(onlyTest instanceof TestSuite, "Only test suites can be run in shared session mode");
		TestSuite nested = (TestSuite) onlyTest;
		try {
			// in shared mode no TestDescriptors are used, need to set up environment ourselves
			Setup localSetup = (Setup) getSetup().clone();
			localSetup.setEclipseArgument(Setup.APPLICATION, applicationId);
			localSetup.setEclipseArgument("testpluginname", pluginId);
			localSetup.setEclipseArgument("classname", (nested.getName() != null ? nested.getName() : nested.getClass().getName()));
			// run the session tests
			new SessionTestRunner().run(this, result, localSetup, false);
		} catch (SetupException e) {
			result.addError(this, e.getCause());
			return;
		}
	}

	protected void runSessionTest(TestDescriptor test, TestResult result) {
		try {
			fillTestDescriptor(test);
			test.run(result);
		} catch (SetupException e) {
			result.addError(test.getTest(), e.getCause());
		}
	}

	public final void runTest(Test test, TestResult result) {
		if (sharedSession)
			// just for safety, prevent anybody from calling this API - we don't run individual tests when in shared mode
			throw new UnsupportedOperationException();

		if (test instanceof TestDescriptor)
			runSessionTest((TestDescriptor) test, result);
		else if (test instanceof TestCase) {
			if (isLocalTest(test))
				// local, ordinary test - just run it
				test.run(result);
			else
				runSessionTest(new TestDescriptor((TestCase) test), result);
		} else if (test instanceof TestSuite)
			// find and run the test cases that make up the suite
			runTestSuite((TestSuite) test, result);
		else
			// we don't support session tests for things that are not TestCases 
			// or TestSuites (e.g. TestDecorators) 
			test.run(result);
	}

	/*
	 * Traverses the test suite to find individual test cases to be run with the SessionTestRunner.
	 */
	protected void runTestSuite(TestSuite suite, TestResult result) {
		for (Enumeration e = suite.tests(); e.hasMoreElements();) {
			if (result.shouldStop())
				break;
			Test test = (Test) e.nextElement();
			runTest(test, result);
		}
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	void setSetup(Setup setup) {
		this.setup = setup;
	}

	public void setSharedSession(boolean sharedSession) {
		this.sharedSession = sharedSession;
	}
}
