package com.aptana.editor.sass.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.sass.SassCodeScannerTest;
import com.aptana.editor.sass.SassSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.sass.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(SassSourcePartitionScannerTest.class);
		suite.addTestSuite(SassCodeScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
