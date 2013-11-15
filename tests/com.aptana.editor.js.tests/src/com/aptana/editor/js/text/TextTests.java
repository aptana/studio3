/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.text;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

/**
 * @author Ingo Muschenetz
 */
public class TextTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.text")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(JSAutoIndentStrategyTest.class);
		suite.addTestSuite(JSCodeScannerTest.class);
		suite.addTestSuite(JSDocScannerTest.class);
		suite.addTestSuite(JSDoubleQuotedStringScannerTest.class);
		suite.addTestSuite(JSRegexScannerTest.class);
		suite.addTestSuite(JSSingleQuotedStringScannerTest.class);
		suite.addTestSuite(JSSourcePartitionScannerJFlexTest.class);
		suite.addTestSuite(JSTextHoverTest.class);
		// $JUnit-END$
		return suite;
	}

}
