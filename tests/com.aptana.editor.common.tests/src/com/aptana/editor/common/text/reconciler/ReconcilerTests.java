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
		// Please do not include ad-hoc performance test classes in here. 
		// They have no pass/fail and just slow down the build!
//		suite.addTestSuite(RubyRegexpFolderPerformanceTest.class); 
		// $JUnit-END$
		return suite;
	}

}
