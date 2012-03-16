package com.aptana.parsing.lexer;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class LexerTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(LexerTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(LexemeTest.class);
		suite.addTestSuite(RangeTest.class);
		//$JUnit-END$
		return suite;
	}

}
