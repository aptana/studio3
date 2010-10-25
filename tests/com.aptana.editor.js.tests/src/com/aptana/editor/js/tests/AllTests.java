package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All tests for com.aptana.editor.js");
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.js.AllTests.suite());
		suite.addTest(com.aptana.editor.js.contentassist.AllTests.suite());
		suite.addTest(com.aptana.editor.js.index.AllTests.suite());
		suite.addTest(com.aptana.editor.js.inferencing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.js.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.sdoc.parsing.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
