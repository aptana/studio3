package com.aptana.editor.markdown.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.markdown.text.rules.MarkdownPartitionScannerTest;
import com.aptana.editor.markdown.text.rules.MarkdownScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(MarkdownPartitionScannerTest.class);
		suite.addTestSuite(MarkdownScannerTest.class);
		// $JUnit-END$
		return suite;
	}

}
