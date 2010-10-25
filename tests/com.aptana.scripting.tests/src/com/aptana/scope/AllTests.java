package com.aptana.scope;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.scope");
		// $JUnit-BEGIN$
		suite.addTestSuite(ScopeSelectorTests.class);
		suite.addTestSuite(AndSelectorTests.class);
		suite.addTestSuite(NameSelectorTests.class);
		suite.addTestSuite(OrSelectorTests.class);
		suite.addTestSuite(NegativeLookaheadTests.class);
		// $JUnit-END$
		return suite;
	}
}
