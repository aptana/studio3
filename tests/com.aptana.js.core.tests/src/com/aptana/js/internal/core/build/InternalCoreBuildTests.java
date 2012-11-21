package com.aptana.js.internal.core.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class InternalCoreBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(InternalCoreBuildTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JSLintValidatorTest.class);
		suite.addTestSuite(JSParserValidatorTest.class);
		suite.addTestSuite(JSStyleValidatorTest.class);
		//$JUnit-END$
		return suite;
	}

}
