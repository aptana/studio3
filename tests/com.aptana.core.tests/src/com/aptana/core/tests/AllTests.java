/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import ch.randelshofer.quaqua.util.BinaryPListParserTest;

import com.aptana.core.util.AllUtilTests;
import com.aptana.plist.xml.XMLPListParserTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.core.tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(IdeLogTest.class);
		suite.addTestSuite(BinaryPListParserTest.class);
		suite.addTestSuite(XMLPListParserTest.class);
		suite.addTest(AllUtilTests.suite());
		// $JUnit-END$
		return suite;
	}
}
