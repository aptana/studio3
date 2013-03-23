package com.aptana.js.core.parsing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CoreParsingTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreParsingTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(JSFlexScannerTest.class);
		suite.addTestSuite(JSParserTest.class);
		suite.addTestSuite(SDocNodeAttachmentTest.class);
		//$JUnit-END$
		return suite;
	}

}
