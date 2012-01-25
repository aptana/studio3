package com.aptana.buildpath.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.core.build.AbstractBuildParticipantTest;
import com.aptana.core.build.BuildParticipantManagerTest;

public class BuildPathCoreTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(BuildPathCoreTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(AbstractBuildParticipantTest.class);
		suite.addTestSuite(BuildParticipantManagerTest.class);
		// $JUnit-END$
		return suite;
	}

}
