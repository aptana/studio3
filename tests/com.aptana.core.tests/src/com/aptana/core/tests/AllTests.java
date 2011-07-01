/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.core.util.AllUtilTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.core.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(IdeLogTest.class);
		suite.addTest(AllUtilTests.suite());
		// $JUnit-END$
		return suite;
	}
}
