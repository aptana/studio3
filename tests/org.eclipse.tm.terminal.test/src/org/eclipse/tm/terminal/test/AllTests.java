/*******************************************************************************
 * Copyright (c) 2008, 2010 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Oberhuber (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.terminal.test;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * Master test suite to run all terminal unit tests.
 */
public class AllTests
{

	/**
	 * Call each AllTests class from each of the test packages.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		suite.addTest(AutomatedTests.suite());
		suite.addTest(AutomatedPluginTests.suite());
		return suite;
	}
}
