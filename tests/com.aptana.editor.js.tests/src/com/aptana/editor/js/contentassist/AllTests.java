package com.aptana.editor.js.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(LocationTests.class);
		suite.addTestSuite(ASTQueryTests.class);
		//suite.addTestSuite(RangeTests.class);
		suite.addTestSuite(MetadataTests.class);
		//$JUnit-END$
		return suite;
	}

}
