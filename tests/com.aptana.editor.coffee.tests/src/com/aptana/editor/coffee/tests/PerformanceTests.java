package com.aptana.editor.coffee.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.editor.coffee.parsing.lexer.CoffeeScannerPerformanceTest;

public class PerformanceTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CoffeeScannerPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
