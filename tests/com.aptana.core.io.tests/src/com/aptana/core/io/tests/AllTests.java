package com.aptana.core.io.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(EFSUtilsTest.class);
		suite.addTestSuite(WorkspaceFileSystemTest.class);
		// $JUnit-END$
		return suite;
	}
}
