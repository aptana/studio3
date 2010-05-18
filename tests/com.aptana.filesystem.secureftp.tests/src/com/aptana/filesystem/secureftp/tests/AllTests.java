package com.aptana.filesystem.secureftp.tests;

import org.kohsuke.junit.ParallelTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new ParallelTestSuite(AllTests.class.getName(), 2);
		// $JUnit-BEGIN$
		suite.addTestSuite(SFTPConnectionTest.class);
		suite.addTestSuite(ImplicitFTPSConnectionTest.class);
		suite.addTestSuite(FTPSConnectionTest.class);
		suite.addTestSuite(FTPSConnectionWithBasePathTest.class);
		// $JUnit-END$
		return suite;
	}
}
