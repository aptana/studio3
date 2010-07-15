package com.aptana.core.tests;

import com.aptana.core.util.CollectionsUtilTest;
import com.aptana.core.util.IOUtilTest;
import com.aptana.core.util.StringUtilTest;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.core.util.tests");
		//$JUnit-BEGIN$
		suite.addTestSuite(StringUtilTest.class);
		suite.addTestSuite(IOUtilTest.class);
		suite.addTestSuite(CollectionsUtilTest.class);
		//$JUnit-END$
		return suite;
	}

}
