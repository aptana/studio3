package com.aptana.index.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.index.core.IndexTest;

public class AllIndexCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllIndexCoreTests.class.getName());
		// $JUnit-BEGIN$
		// suite.addTestSuite(DiskIndexTest.class);
		suite.addTestSuite(IndexTest.class);
		// $JUnit-END$
		return suite;
	}

}
