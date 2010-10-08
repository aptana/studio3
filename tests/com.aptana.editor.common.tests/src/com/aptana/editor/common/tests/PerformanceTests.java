package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.internal.peer.PeerCharacterCloserPerfTest;
import com.aptana.editor.common.text.reconciler.RubyRegexpFolderPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(PeerCharacterCloserPerfTest.class);
		suite.addTestSuite(RubyRegexpFolderPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
