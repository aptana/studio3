package com.aptana.scripting.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.scripting.model");
		// $JUnit-BEGIN$
		suite.addTestSuite(BundleTests.class);
		suite.addTestSuite(CommandTests.class);
		suite.addTestSuite(ContextTests.class);
		suite.addTestSuite(FilterTests.class);
		suite.addTestSuite(KeyBindingTests.class);
		suite.addTestSuite(BundleVisibilityTests.class);
		// $JUnit-END$
		return suite;
	}
}
