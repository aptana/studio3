package com.aptana.parsing.ast;

import junit.framework.Test;
import junit.framework.TestSuite;

@SuppressWarnings("nls")
public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.parsing.ast");
		//$JUnit-BEGIN$
		suite.addTestSuite(ParseNodeTests.class);
		//$JUnit-END$
		return suite;
	}

}
