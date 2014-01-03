/*******************************************************************************
 * Copyright (c) 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Martin Oberhuber (Wind River) - initial API and implementation
 *******************************************************************************/
package org.eclipse.tm.internal.terminal.emulator;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Terminal emulator test cases. Runs in emulator package to allow access to default visible items.
 */
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		suite.addTestSuite(VT100EmulatorBackendTest.class);
		return suite;
	}

}
