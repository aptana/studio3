package com.aptana.scripting.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.scripting.model");
		// $JUnit-BEGIN$
		suite.addTestSuite(BundleLoadingTests.class);
		suite.addTestSuite(CommandTests.class);
		// $JUnit-END$
		return suite;
	}
}
