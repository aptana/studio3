package com.aptana.theme;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ThemeTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ThemeTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(TextmateImporterTest.class);
		suite.addTestSuite(ThemeTest.class);
		//$JUnit-END$
		return suite;
	}

}
