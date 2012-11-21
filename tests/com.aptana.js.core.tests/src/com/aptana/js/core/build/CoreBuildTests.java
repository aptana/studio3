package com.aptana.js.core.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CoreBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreBuildTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JSTaskDetectorTest.class);
		//$JUnit-END$
		return suite;
	}

}
