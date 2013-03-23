package com.aptana.dtd.core.parsing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class DTDCoreParsingTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(DTDCoreParsingTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(DTDParserTest.class);
		// $JUnit-END$
		return suite;
	}

}
