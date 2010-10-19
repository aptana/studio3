package com.aptana.filesystem.s3.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(S3ConnectionPointTest.class);
		//$JUnit-END$
		return suite;
	}

}
