package com.aptana.git.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.git.core.GitCoreTests;
import com.aptana.git.core.model.CoreModelTests;
import com.aptana.git.internal.core.storage.CoreStorageTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(CoreModelTests.suite());
		suite.addTest(CoreStorageTests.suite());
		suite.addTest(GitCoreTests.suite());
		// $JUnit-END$
		return suite;
	}

}
