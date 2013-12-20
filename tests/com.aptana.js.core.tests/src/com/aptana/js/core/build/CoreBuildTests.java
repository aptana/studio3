package com.aptana.js.core.build;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({JSTaskDetectorTest.class, })
public class CoreBuildTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(CoreBuildTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		//$JUnit-BEGIN$
//		suite.addTestSuite(JSTaskDetectorTest.class);
//		//$JUnit-END$
//		return suite;
//	}
//
}
