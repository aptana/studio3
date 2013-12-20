/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.contentassist;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({ContentAssistFineLocationTests.class, ContentAssistCoarseLocationTests.class, HTMLContentAssistProcessorTest.class, MetadataTests.class, HTMLNestedLanguageContentAssistTests.class, })
public class AllTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Test for com.aptana.editor.html.contentassist")
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(ContentAssistFineLocationTests.class);
//		suite.addTestSuite(ContentAssistCoarseLocationTests.class);
//		suite.addTestSuite(HTMLContentAssistProcessorTest.class);
//		suite.addTestSuite(MetadataTests.class);
//		suite.addTestSuite(HTMLNestedLanguageContentAssistTests.class);
//		// $JUnit-END$
//		return suite;
//	}
//
}
