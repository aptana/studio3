package com.aptana.red.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

public class CoreTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.git.core.tests.AllTests.suite());
		suite.addTest(com.aptana.util.tests.AllTests.suite());
		suite.addTest(com.aptana.scripting.tests.AllTests.suite());
		/*
		suite.addTest(com.aptana.filesystem.ftp.tests.AllTests.suite());
		suite.addTest(com.aptana.filesystem.secureftp.tests.AllTests.suite());
		*/
		// $JUnit-END$
		return suite;
	}
}
