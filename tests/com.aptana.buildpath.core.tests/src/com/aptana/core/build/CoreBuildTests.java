package com.aptana.core.build;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CoreBuildTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreBuildTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(AbstractBuildParticipantTest.class);
		suite.addTestSuite(ReconcileContextTest.class);
		suite.addTestSuite(RequiredBuildParticipantTest.class);
		suite.addTestSuite(UnifiedBuilderTest.class);
		// $JUnit-END$
		return suite;
	}

}
