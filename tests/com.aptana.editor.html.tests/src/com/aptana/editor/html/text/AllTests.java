package com.aptana.editor.html.text;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSElementSelectorHoverTest.class);
		suite.addTestSuite(HTMLTextHoverTest.class);
		// $JUnit-END$
		return suite;
	}

}
