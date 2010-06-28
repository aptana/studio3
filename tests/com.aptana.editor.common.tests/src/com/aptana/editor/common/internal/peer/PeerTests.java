package com.aptana.editor.common.internal.peer;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PeerTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PeerTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(PeerCharacterCloserTest.class);
		// Please do not include ad-hoc performance test classes in here. 
		// They have no pass/fail and just slow down the build!
//		suite.addTestSuite(PeerCharacterCloserPerfTest.class);
		suite.addTestSuite(CharacterPairMatcherTest.class);
		suite.addTestSuite(ExitPolicyTest.class);
		// $JUnit-END$
		return suite;
	}

}
