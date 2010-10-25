package com.aptana.theme.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.theme.ThemeTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(ThemeTests.suite());
		// $JUnit-END$
		return suite;
	}

}
