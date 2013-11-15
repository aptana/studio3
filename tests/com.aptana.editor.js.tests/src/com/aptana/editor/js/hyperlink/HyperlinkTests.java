package com.aptana.editor.js.hyperlink;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class HyperlinkTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(HyperlinkTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(JSHyperlinkDetectorTests.class);
		// $JUnit-END$
		return suite;
	}

}
