package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSSourcePartitionScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
