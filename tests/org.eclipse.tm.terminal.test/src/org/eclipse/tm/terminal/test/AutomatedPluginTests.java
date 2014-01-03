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
 * Master Test Suite to run all Terminal plug-in tests.
 */
public class AutomatedPluginTests
{
	/**
	 * Call each AllTests class from each of the test packages.
	 */
	public static Test suite()
	{
		TestSuite suite = new TestSuite(AutomatedPluginTests.class.getName());
		// These tests require Eclipse Platform to be up
		suite.addTestSuite(org.eclipse.tm.internal.terminal.connector.TerminalConnectorPluginTest.class);
		suite.addTestSuite(org.eclipse.tm.internal.terminal.connector.TerminalConnectorFactoryTest.class);

		// These tests must run as plain JUnit because they require access
		// to "package" protected methods
		// suite.addTest(AutomatedTests.suite());
		return suite;
	}

}
