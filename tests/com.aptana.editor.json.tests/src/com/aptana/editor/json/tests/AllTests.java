package com.aptana.editor.json.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.json.internal.text.JSONFoldingComputerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(JSONFoldingComputerTest.class);
		// $JUnit-END$
		return suite;
	}

}
