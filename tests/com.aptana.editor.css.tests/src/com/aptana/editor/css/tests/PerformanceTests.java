package com.aptana.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerPerformanceTest;
import com.aptana.editor.css.CSSParserPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance Tests for com.aptana.editor.css plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerPerformanceTest.class);
		suite.addTestSuite(CSSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
