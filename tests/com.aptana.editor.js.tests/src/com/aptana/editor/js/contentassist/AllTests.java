package com.aptana.editor.js.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.js.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(LocationTests.class);
		suite.addTestSuite(ASTQueryTests.class);
		//$JUnit-END$
		return suite;
	}

}
