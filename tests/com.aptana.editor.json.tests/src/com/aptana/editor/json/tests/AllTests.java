/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.json.tests;

import com.aptana.editor.json.outline.JSONOutlineProviderTest;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class AllTests
{

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
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.json.AllTests.suite());
		suite.addTestSuite(JSONOutlineProviderTest.class);
		suite.addTest(com.aptana.editor.json.internal.text.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
