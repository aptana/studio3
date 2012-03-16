package com.aptana.index.core;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class IndexCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(IndexCoreTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(FileStoreBuildContextTest.class);
		suite.addTestSuite(IndexContainerJobTest.class);
		suite.addTestSuite(IndexTest.class);
		//$JUnit-END$
		return suite;
	}

}
