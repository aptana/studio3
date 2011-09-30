package com.aptana.editor.common.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class TextTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(TextTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(RubyRegexpAutoIndentStrategyTest.class);
		suite.addTestSuite(SingleTokenScannerTest.class);
		//$JUnit-END$
		return suite;
	}

}
