package com.aptana.js.internal.core.parsing.sdoc;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({SDocFlexScannerTest.class, SDocParserTest.class, SDocTypeTokenScannerTest.class, })
public class InternalCoreParsingSDocTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(InternalCoreParsingSDocTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(SDocFlexScannerTest.class);
//		suite.addTestSuite(SDocParserTest.class);
//		suite.addTestSuite(SDocTypeTokenScannerTest.class);
//		// $JUnit-END$
//		return suite;
//	}
//
}
