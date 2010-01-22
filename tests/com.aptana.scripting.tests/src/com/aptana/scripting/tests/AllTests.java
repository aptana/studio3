package com.aptana.scripting.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.scripting.BundleConverterTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.scripting.model.AllTests.suite());
		suite.addTest(com.aptana.scope.AllTests.suite());
		suite.addTestSuite(BundleConverterTest.class);
		// $JUnit-END$
		return suite;
	}

}
