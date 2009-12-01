package com.aptana.radrails.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.radrails.editor.css.CSSSourcePartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.radrails.editor.css.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSSourcePartitionScannerTest.class);
		// $JUnit-END$
		return suite;
	}
}
