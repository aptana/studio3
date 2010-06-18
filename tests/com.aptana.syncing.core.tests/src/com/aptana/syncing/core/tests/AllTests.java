package com.aptana.syncing.core.tests;

import org.kohsuke.junit.ParallelTestSuite;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new ParallelTestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(LocalSyncingTests.class);
		suite.addTestSuite(LocalSyncingTestsWithSpaces.class);
		suite.addTestSuite(FTPSyncingTests.class);
		suite.addTestSuite(FTPSyncingTestsWithSpaces.class);
		suite.addTestSuite(SFTPSyncingTests.class);
		suite.addTestSuite(SFTPSyncingTestsWithSpaces.class);
		suite.addTestSuite(LocalLargeSampleSyncingTests.class);
		suite.addTestSuite(FTPLargeSampleSyncingTests.class);

		// $JUnit-END$
		return suite;
	}
}
