package com.aptana.editor.sass.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.sass.SassCodeScannerPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance tests for com.aptana.editor.sass plugin"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(SassCodeScannerPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
