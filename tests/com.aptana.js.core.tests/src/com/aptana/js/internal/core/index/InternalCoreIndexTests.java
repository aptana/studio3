package com.aptana.js.internal.core.index;

import org.junit.runners.Suite.SuiteClasses;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

@RunWith(Suite.class)
@SuiteClasses({JSIndexTest.class, JSMetadataIndexWriterTest.class, MetadataTest.class, })
public class InternalCoreIndexTests
{

//	public static Test suite()
//	{
//		TestSuite suite = new TestSuite(InternalCoreIndexTests.class.getName())
//		{
//			@Override
//			public void runTest(Test test, TestResult result)
//			{
//				System.err.println("Running test: " + test.toString());
//				super.runTest(test, result);
//			}
//		};
//		// $JUnit-BEGIN$
//		suite.addTestSuite(JSIndexTest.class);
//		suite.addTestSuite(JSMetadataIndexWriterTest.class);
//		suite.addTestSuite(MetadataTest.class);
//		// $JUnit-END$
//		return suite;
//	}
//
}
