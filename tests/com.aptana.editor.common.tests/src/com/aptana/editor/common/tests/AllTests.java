package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.EditorCommonTests;
import com.aptana.editor.common.peer.PeerTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(EditorCommonTests.suite());
		suite.addTest(PeerTests.suite());
		// $JUnit-END$
		return suite;
	}

}
