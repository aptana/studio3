package com.aptana.js.internal.core.index;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class InternalCoreIndexTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(InternalCoreIndexTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(JSIndexTest.class);
		suite.addTestSuite(JSMetadataIndexWriterTest.class);
		suite.addTestSuite(MetadataTest.class);
		// $JUnit-END$
		return suite;
	}

}
