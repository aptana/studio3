package com.aptana.editor.common.text;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({RubyRegexpAutoIndentStrategyTest.class, SingleTokenScannerTest.class, })
public class TextTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(TextTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		//$JUnit-BEGIN$
//		suite.addTestSuite(RubyRegexpAutoIndentStrategyTest.class);
//		suite.addTestSuite(SingleTokenScannerTest.class);
//		//$JUnit-END$
//		return suite;
//	}
//
}
