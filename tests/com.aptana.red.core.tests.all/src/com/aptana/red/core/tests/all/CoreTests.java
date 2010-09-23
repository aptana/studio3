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
		suite.addTest(com.aptana.core.tests.AllTests.suite());
		suite.addTest(com.aptana.core.io.tests.AllTests.suite());
		suite.addTest(com.aptana.scripting.tests.AllTests.suite());
		suite.addTest(com.aptana.filesystem.ftp.tests.AllTests.suite());
		suite.addTest(com.aptana.filesystem.secureftp.tests.AllTests.suite());
		suite.addTest(com.aptana.parsing.tests.AllTests.suite());
		suite.addTest(com.aptana.plist.tests.AllTests.suite());
		// suite.addTest(com.aptana.syncing.core.tests.AllTests.suite()); // Disables for now as it's causing hudson build to time out
		// $JUnit-END$
		return suite;
	}
}
