package com.aptana.editor.js.validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ValidatorTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JSLintValidatorTest.class);
		suite.addTestSuite(JSParserValidatorTest.class);
		suite.addTestSuite(JSStyleValidatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
