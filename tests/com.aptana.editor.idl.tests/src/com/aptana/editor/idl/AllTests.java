package com.aptana.editor.idl;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.idl"); //$NON-NLS-1$
		//$JUnit-BEGIN$
		suite.addTestSuite(IDLSouceScannerTests.class);
		//suite.addTestSuite(IDLParserTests.class);
		//$JUnit-END$
		return suite;
	}
}
