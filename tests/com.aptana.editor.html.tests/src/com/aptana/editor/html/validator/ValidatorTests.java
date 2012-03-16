package com.aptana.editor.html.validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ValidatorTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLParseErrorValidatorTest.class);
		// suite.addTestSuite(HTMLTidyValidatorPerformanceTest.class);
		suite.addTestSuite(HTMLTidyValidatorTest.class);
		// $JUnit-END$
		return suite;
	}

}
