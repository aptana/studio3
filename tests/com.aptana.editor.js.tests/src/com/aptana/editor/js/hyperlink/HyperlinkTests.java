package com.aptana.editor.js.hyperlink;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class HyperlinkTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(HyperlinkTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(JSHyperlinkDetectorTests.class);
		// $JUnit-END$
		return suite;
	}

}
