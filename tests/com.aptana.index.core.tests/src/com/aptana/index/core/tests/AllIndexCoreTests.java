package com.aptana.index.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.index.core.IndexCoreTests;
import com.aptana.index.core.build.BuildContextTest;
import com.aptana.internal.index.core.DiskIndexTest;

public class AllIndexCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllIndexCoreTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(DiskIndexTest.class);
		suite.addTestSuite(BuildContextTest.class);
		suite.addTest(IndexCoreTests.suite());
		// $JUnit-END$
		return suite;
	}

}
