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
import junit.framework.TestSuite;

/**
 * Master test suite to run all terminal unit tests.
 */
public class AutomatedTests
{

	public static final String PI_TERMINAL_TESTS = "org.eclipse.tm.terminal.test"; //$NON-NLS-1$

	/**
	 * Call each AllTests class from each of the test packages.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(AutomatedTests.class.getName());
		suite.addTest(org.eclipse.tm.internal.terminal.emulator.AllTests.suite());
		suite.addTest(org.eclipse.tm.internal.terminal.model.AllTests.suite());
		suite.addTest(org.eclipse.tm.terminal.model.AllTests.suite());
		suite.addTestSuite(org.eclipse.tm.internal.terminal.connector.TerminalConnectorTest.class);
		suite.addTestSuite(org.eclipse.tm.internal.terminal.connector.TerminalToRemoteInjectionOutputStreamTest.class);
		return suite;
	}
}
