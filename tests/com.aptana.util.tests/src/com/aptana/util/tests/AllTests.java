package com.aptana.util.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.util.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(StringUtilTest.class);
		suite.addTestSuite(IOUtilTest.class);
		suite.addTestSuite(CollectionsUtilTest.class);
		//$JUnit-END$
		return suite;
	}

}
