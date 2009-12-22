package com.aptana.editor.common.peer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PeerTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PeerTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(CharacterPairMatcherTest.class);
		//$JUnit-END$
		return suite;
	}

}
