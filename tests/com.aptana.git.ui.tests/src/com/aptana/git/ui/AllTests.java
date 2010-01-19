package com.aptana.git.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.git.ui.internal.DiffFormatterTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(DiffFormatterTest.class);
		// $JUnit-END$
		return suite;
	}

}
