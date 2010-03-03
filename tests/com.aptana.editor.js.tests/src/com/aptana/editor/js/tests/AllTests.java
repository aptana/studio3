package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.JSCodeScannerTest;
import com.aptana.editor.js.JSParserPerformanceTest;
import com.aptana.editor.js.JSParserTest;
import com.aptana.editor.js.JSSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.js.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSSourcePartitionScannerTest.class);
		suite.addTestSuite(JSCodeScannerTest.class);
		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSParserTest.class);
		suite.addTestSuite(JSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
