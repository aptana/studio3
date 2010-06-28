package com.aptana.editor.html.outline;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.ide.editor.html.outline");
		// $JUnit-BEGIN$
		suite.addTestSuite(HTMLOutlineProviderTest.class);
		// $JUnit-END$
		return suite;
	}
}
