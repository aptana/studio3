package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.js.tests");
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.js.AllTests.suite());
		suite.addTest(com.aptana.editor.js.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.js.index.AllTests.suite());
		suite.addTest(com.aptana.editor.js.contentassist.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
