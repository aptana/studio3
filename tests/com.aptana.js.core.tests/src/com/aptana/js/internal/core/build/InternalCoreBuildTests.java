package com.aptana.js.internal.core.build;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({JSLintValidatorTest.class, JSParserValidatorTest.class, JSStyleValidatorTest.class, })
public class InternalCoreBuildTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(InternalCoreBuildTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		//$JUnit-BEGIN$
//		suite.addTestSuite(JSLintValidatorTest.class);
//		suite.addTestSuite(JSParserValidatorTest.class);
//		suite.addTestSuite(JSStyleValidatorTest.class);
//		//$JUnit-END$
//		return suite;
//	}
//
}
