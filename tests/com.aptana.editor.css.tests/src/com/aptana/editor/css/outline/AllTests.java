package com.aptana.editor.css.outline;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.ide.editor.css.outline");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSOutlineProviderTest.class);
		// $JUnit-END$
		return suite;
	}
}
