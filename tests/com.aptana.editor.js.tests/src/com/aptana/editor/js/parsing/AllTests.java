package com.aptana.editor.js.parsing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSParserTest.class);
		// suite.addTestSuite(JSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
