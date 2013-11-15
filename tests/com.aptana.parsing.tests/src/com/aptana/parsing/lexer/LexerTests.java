package com.aptana.parsing.lexer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class LexerTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(LexerTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		//$JUnit-BEGIN$
		suite.addTestSuite(LexemeTest.class);
		suite.addTestSuite(RangeTest.class);
		//$JUnit-END$
		return suite;
	}

}
