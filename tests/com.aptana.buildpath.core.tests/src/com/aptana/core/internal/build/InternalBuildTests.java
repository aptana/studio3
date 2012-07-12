package com.aptana.core.internal.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class InternalBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(InternalBuildTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(BuildParticipantWorkingCopyTest.class);
		suite.addTestSuite(BuildParticipantManagerTest.class);
		suite.addTestSuite(IndexBuildParticipantTest.class);
		// $JUnit-END$
		return suite;
	}

}
