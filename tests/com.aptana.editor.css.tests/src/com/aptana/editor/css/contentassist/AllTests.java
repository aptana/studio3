/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.contentassist;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({CoarseLocationTests.class, CSSContentAssistProposalTests.class, CSSTextHoverTests.class, FineLocationTests.class, RangeTests.class, })
public class AllTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.contentassist")
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(CoarseLocationTests.class);
//		suite.addTestSuite(CSSContentAssistProposalTests.class);
//		suite.addTestSuite(CSSTextHoverTests.class);
//		suite.addTestSuite(FineLocationTests.class);
//		suite.addTestSuite(RangeTests.class);
//		// $JUnit-END$
//		return suite;
//	}
//
}
