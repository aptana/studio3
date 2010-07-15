package com.aptana.editor.css.contentassist;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.editor.css.contentassist");
		//$JUnit-BEGIN$
		suite.addTestSuite(FineLocationTests.class);
		suite.addTestSuite(CoarseLocationTests.class);
		suite.addTestSuite(RangeTests.class);
		suite.addTestSuite(MetadataTests.class);
		//$JUnit-END$
		return suite;
	}

}
