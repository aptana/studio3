package com.aptana.git.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.git.core.model.GitExecutableTest;
import com.aptana.git.core.model.GitRepositoryTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(GitExecutableTest.class);
		suite.addTestSuite(GitRepositoryTest.class);
		// $JUnit-END$
		return suite;
	}

}
