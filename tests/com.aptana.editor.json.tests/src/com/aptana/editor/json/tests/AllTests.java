package com.aptana.editor.json.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.json.AllTests.suite());
		suite.addTest(com.aptana.editor.json.internal.text.AllTests.suite());
		// $JUnit-END$
		return suite;
	}

}
