package com.aptana.editor.js.validator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ValidatorPerformanceTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ValidatorPerformanceTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JSStyleValidatorPerformanceTest.class);
		suite.addTestSuite(JSLintValidatorPerformanceTest.class);
		//$JUnit-END$
		return suite;
	}

}
