package com.aptana.editor.markdown.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.markdown.MarkdownPartitionScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(MarkdownPartitionScannerTest.class);
		// $JUnit-END$
		return suite;
	}

}
