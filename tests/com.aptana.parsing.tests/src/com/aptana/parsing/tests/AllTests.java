/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.parsing.tests;

import com.aptana.parsing.ParseStateCacheKeyWithCommentsTest;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@SuppressWarnings("nls")
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.parsing")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(ParseStateCacheKeyWithCommentsTest.class);
		suite.addTestSuite(ParseStateTest.class);
		suite.addTest(com.aptana.json.AllTests.suite());
		suite.addTest(com.aptana.parsing.ast.AllTests.suite());
		suite.addTest(com.aptana.parsing.lexer.LexerTests.suite());
		suite.addTest(com.aptana.parsing.pool.AllTests.suite());
		suite.addTest(com.aptana.sax.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
