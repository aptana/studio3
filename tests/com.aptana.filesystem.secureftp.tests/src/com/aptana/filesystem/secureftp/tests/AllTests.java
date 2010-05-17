package com.aptana.filesystem.secureftp.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(FTPSConnectionTest.suite());
		suite.addTest(FTPSConnectionWithBasePathTest.suite());
		suite.addTest(SFTPConnectionTest.suite());
		suite.addTestSuite(ImplicitFTPSConnectionTest.class);
		// $JUnit-END$
		return suite;
	}
}
