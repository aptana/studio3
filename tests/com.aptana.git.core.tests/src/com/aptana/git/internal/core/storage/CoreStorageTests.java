package com.aptana.git.internal.core.storage;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CoreStorageTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreStorageTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CommitFileRevisionTest.class);
		suite.addTestSuite(GitFileHistoryProviderTest.class);
		suite.addTestSuite(GitFileHistoryTest.class);
		// $JUnit-END$
		return suite;
	}

}
