package com.aptana.filesystem.secureftp.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(FTPConnectionTest.class);
		suite.addTestSuite(FTPConnectionWithBasePathTest.class);
		suite.addTestSuite(FTPSConnectionTest.class);
		suite.addTestSuite(FTPSConnectionWithBasePathTest.class);
		suite.addTestSuite(ImplicitFTPSConnectionTest.class);
		suite.addTestSuite(SFTPConnectionTest.class);
		// $JUnit-END$
		return suite;
	}
}
