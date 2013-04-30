/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.html.contentassist.index.HTMLFileIndexingParticipantTest;
import com.aptana.editor.html.internal.build.HTMLTaskDetectorTest;
import com.aptana.editor.html.validator.ValidatorTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.html.tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.html.HTMLEditorTests.suite());
		suite.addTest(com.aptana.editor.html.parsing.HTMLParsingTests.suite());
		suite.addTest(com.aptana.editor.html.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.html.contentassist.AllTests.suite());
		suite.addTest(ValidatorTests.suite());
		suite.addTest(com.aptana.editor.html.text.AllTests.suite());
		suite.addTestSuite(HTMLFileIndexingParticipantTest.class);
		suite.addTestSuite(HTMLTaskDetectorTest.class);
		// $JUnit-END$
		return suite;
	}
}
