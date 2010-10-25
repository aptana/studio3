package com.aptana.git.core.model;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CoreModelTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreModelTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(GitExecutableTest.class);
		suite.addTestSuite(GitRefTest.class);
		suite.addTestSuite(GitRevSpecifierTest.class);
		suite.addTestSuite(GitRepositoryTest.class);
		//$JUnit-END$
		return suite;
	}

}
