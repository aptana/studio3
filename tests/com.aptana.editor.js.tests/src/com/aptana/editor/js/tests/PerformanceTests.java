package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSCodeScannerPerformanceTest;
import com.aptana.editor.js.contentassist.JSIndexingPerformanceTest;
import com.aptana.editor.js.parsing.JSParserPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance tests for com.aptana.editor.js plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSCodeScannerPerformanceTest.class);
		suite.addTestSuite(JSIndexingPerformanceTest.class);
		suite.addTestSuite(JSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
