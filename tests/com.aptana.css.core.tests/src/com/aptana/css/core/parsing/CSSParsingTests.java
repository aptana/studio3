/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.parsing;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({CSSIdentifierTest.class, CSSKeywordTest.class, CSSLiteralTest.class, CSSNotTest.class, CSSParserTest.class, CSSPunctuatorTest.class, CSSSpecialTokenHandlingTest.class, })
public class CSSParsingTests
{
//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Test for com.aptana.css.core.parsing")
//		{
//			/*
//			 * (non-Javadoc)
//			 * @see junit.framework.TestSuite#run(junit.framework.TestResult)
//			 */
//			@Override
//			public void run(TestResult result)
//			{
//				super.run(result);
//
//				// Verify that all CSS token types were tested
//				if (!result.shouldStop())
//				{
//					runTest(new VerifyTestedTokensTest(), result);
//				}
//			}
//
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//
//		// $JUnit-BEGIN$
//		suite.addTestSuite(CSSIdentifierTest.class);
//		suite.addTestSuite(CSSKeywordTest.class);
//		suite.addTestSuite(CSSLiteralTest.class);
//		suite.addTestSuite(CSSNotTest.class);
//		suite.addTestSuite(CSSParserTest.class);
//		suite.addTestSuite(CSSPunctuatorTest.class);
//		suite.addTestSuite(CSSSpecialTokenHandlingTest.class);
//		// $JUnit-END$
//		return suite;
//	}
}
