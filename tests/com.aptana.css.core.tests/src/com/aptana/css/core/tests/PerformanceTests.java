/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.tests;

import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.css.core.parsing.CSSParserPerformanceTest;
import com.aptana.css.core.parsing.CSSScannerPerformanceTest;

public class PerformanceTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("CSS Core performance tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				String msg = MessageFormat.format("Running test: {0}", test.toString());
				System.err.println(msg);
				super.runTest(test, result);
			}
		};

		// $JUnit-BEGIN$
		suite.addTestSuite(CSSScannerPerformanceTest.class);
		suite.addTestSuite(CSSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
