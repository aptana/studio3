package com.aptana.parsing.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

@SuppressWarnings("nls")
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.parsing");
		//$JUnit-BEGIN$
		suite.addTest(com.aptana.parsing.ast.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
