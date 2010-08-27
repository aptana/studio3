package com.aptana.editor.js.outline;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.outline");
		// $JUnit-BEGIN$
		suite.addTestSuite(SimpleItemsTest.class);
		suite.addTestSuite(InheritanceItemsTest.class);
		suite.addTestSuite(BlockItemsTest.class);
		// $JUnit-END$
		return suite;
	}
}
