package com.aptana.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.html.HTMLTagScannerPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance Tests for com.aptana.editor.html plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLTagScannerPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
