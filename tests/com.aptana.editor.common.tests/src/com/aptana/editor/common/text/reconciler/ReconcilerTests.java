package com.aptana.editor.common.text.reconciler;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ReconcilerTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ReconcilerTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(RubyRegexpFolderTest.class);
		suite.addTestSuite(RubyRegexpFolderPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
