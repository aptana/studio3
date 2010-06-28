package com.aptana.editor.js.index;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.index");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSMetadataIndexWriterTests.class);
		suite.addTestSuite(JSIndexTests.class);
		// $JUnit-END$
		return suite;
	}
}
