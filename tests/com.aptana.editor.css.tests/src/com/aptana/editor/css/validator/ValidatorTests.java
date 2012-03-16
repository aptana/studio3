package com.aptana.editor.css.validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ValidatorTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CSSParserValidatorTest.class);
		suite.addTestSuite(CSSValidatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
