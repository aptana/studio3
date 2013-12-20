/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import java.text.MessageFormat;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.html.HTMLTagScannerPerformanceTest;
import com.aptana.editor.html.parsing.HTMLParserPerformanceTest;
import com.aptana.editor.html.tests.performance.OpenHTMLEditorTest;
import com.aptana.editor.html.validator.HTMLTidyValidatorPerformanceTest;

@RunWith(Suite.class)
@SuiteClasses({HTMLParserPerformanceTest.class, HTMLTagScannerPerformanceTest.class, HTMLTidyValidatorPerformanceTest.class, OpenHTMLEditorTest.class, })
public class PerformanceTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite("Performance Tests for com.aptana.editor.html plugin")
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				String msg = MessageFormat.format("Running test: {0}", test.toString());
//				System.err.println(msg);
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(HTMLParserPerformanceTest.class);
//		suite.addTestSuite(HTMLTagScannerPerformanceTest.class);
//		suite.addTestSuite(HTMLTidyValidatorPerformanceTest.class);
//		suite.addTest(OpenHTMLEditorTest.suite());
//		// $JUnit-END$
//		return suite;
//	}
}
