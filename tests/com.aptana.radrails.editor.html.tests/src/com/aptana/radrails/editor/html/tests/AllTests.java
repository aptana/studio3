package com.aptana.radrails.editor.html.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.radrails.editor.html.HTMLSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.radrails.editor.html.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLSourcePartitionScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
