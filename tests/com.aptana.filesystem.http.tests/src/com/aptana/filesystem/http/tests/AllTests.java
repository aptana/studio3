/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.filesystem.http.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.kohsuke.junit.ParallelTestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new ParallelTestSuite(AllTests.class.getName(), 2)
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(HttpFileSystemTest.class);
		suite.addTestSuite(HttpFileStoreTest.class);
		// $JUnit-END$
		return suite;
	}
}
