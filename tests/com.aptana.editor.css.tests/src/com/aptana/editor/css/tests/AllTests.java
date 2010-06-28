package com.aptana.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerTest;
import com.aptana.editor.css.CSSEditorTest;
import com.aptana.editor.css.CSSSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSSourcePartitionScannerTest.class);
		suite.addTestSuite(CSSCodeScannerTest.class);
//		suite.addTestSuite(CSSCodeScannerPerformanceTest.class);
//		suite.addTestSuite(CSSParserPerformanceTest.class);
		suite.addTestSuite(CSSEditorTest.class);
		suite.addTest(com.aptana.editor.css.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.css.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.css.contentassist.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
