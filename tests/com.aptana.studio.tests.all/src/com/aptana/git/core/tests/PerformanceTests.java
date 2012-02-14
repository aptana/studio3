package com.aptana.git.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.git.core.model.GitIndexPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance Tests for com.aptana.git.core plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(GitIndexPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}