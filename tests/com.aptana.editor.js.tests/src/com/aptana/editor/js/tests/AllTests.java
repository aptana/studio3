package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSCodeScannerTest;
import com.aptana.editor.js.JSDocScannerTest;
import com.aptana.editor.js.JSDoubleQuotedStringScannerTest;
import com.aptana.editor.js.JSParserTest;
import com.aptana.editor.js.JSRegexScannerTest;
import com.aptana.editor.js.JSSingleQuotedStringScannerTest;
import com.aptana.editor.js.JSSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.js.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSSourcePartitionScannerTest.class);
		suite.addTestSuite(JSCodeScannerTest.class);
//		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSDocScannerTest.class);
		suite.addTestSuite(JSDoubleQuotedStringScannerTest.class);
		suite.addTestSuite(JSSingleQuotedStringScannerTest.class);
		suite.addTestSuite(JSRegexScannerTest.class);
		suite.addTestSuite(JSParserTest.class);
//		suite.addTestSuite(JSParserPerformanceTest.class);
		suite.addTest(com.aptana.editor.js.outline.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
