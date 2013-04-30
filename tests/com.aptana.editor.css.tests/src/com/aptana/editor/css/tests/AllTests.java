/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerFlexTest;
import com.aptana.editor.css.CSSCodeScannerTest;
import com.aptana.editor.css.CSSEditorTest;
import com.aptana.editor.css.CSSSourcePartitionScannerFlexTest;
import com.aptana.editor.css.CSSSourcePartitionScannerTest;
import com.aptana.editor.css.internal.text.CSSFoldingComputerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.tests")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerTest.class);
		suite.addTestSuite(CSSCodeScannerFlexTest.class);
		suite.addTestSuite(CSSEditorTest.class);
		suite.addTestSuite(CSSFoldingComputerTest.class);
		suite.addTestSuite(CSSSourcePartitionScannerTest.class);
		suite.addTestSuite(CSSSourcePartitionScannerFlexTest.class);
		suite.addTest(com.aptana.editor.css.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.css.contentassist.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
