package com.aptana.editor.coffee.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.editor.coffee.parsing.CoffeeParserTest;
import com.aptana.editor.coffee.parsing.lexer.CoffeeScannerTest;

public class AllTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CoffeeScannerTest.class);
		suite.addTestSuite(CoffeeParserTest.class);
		//$JUnit-END$
		return suite;
	}

}
