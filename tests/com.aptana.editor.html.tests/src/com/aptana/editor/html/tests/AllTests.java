package com.aptana.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.html.HTMLSourcePartitionScannerTest;
import com.aptana.editor.html.HTMLTagScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.html.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLSourcePartitionScannerTest.class);
		suite.addTestSuite(HTMLTagScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
