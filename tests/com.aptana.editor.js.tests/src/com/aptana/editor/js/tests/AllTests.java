package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.JSCodeScannerTest;
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
		suite.addTestSuite(JSParserTest.class);
		// $JUnit-END$
		return suite;
	}
}
