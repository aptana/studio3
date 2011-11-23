/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerTest;
import com.aptana.editor.css.CSSEditorTest;
import com.aptana.editor.css.CSSSourcePartitionScannerTest;
import com.aptana.editor.css.internal.build.CSSTaskDetectorTest;
import com.aptana.editor.css.internal.text.CSSFoldingComputerTest;
import com.aptana.editor.css.validator.CSSValidatorTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerTest.class);
		suite.addTestSuite(CSSEditorTest.class);
		suite.addTestSuite(CSSFoldingComputerTest.class);
		suite.addTestSuite(CSSSourcePartitionScannerTest.class);
		suite.addTestSuite(CSSValidatorTests.class);
		suite.addTest(com.aptana.editor.css.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.css.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.css.contentassist.AllTests.suite());
		suite.addTest(com.aptana.editor.css.contentassist.index.IndexTests.suite());
		suite.addTestSuite(CSSTaskDetectorTest.class);
		// $JUnit-END$
		return suite;
	}
}
