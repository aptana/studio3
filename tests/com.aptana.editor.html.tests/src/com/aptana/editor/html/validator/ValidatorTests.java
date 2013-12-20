package com.aptana.editor.html.validator;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({HTMLParseErrorValidatorTest.class, HTMLTidyValidatorPerformanceTest.class, HTMLTidyValidatorTest.class, })
public class ValidatorTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(ValidatorTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(HTMLParseErrorValidatorTest.class);
//		// suite.addTestSuite(HTMLTidyValidatorPerformanceTest.class);
//		suite.addTestSuite(HTMLTidyValidatorTest.class);
//		// $JUnit-END$
//		return suite;
//	}
//
}
