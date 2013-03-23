package com.aptana.js.core.inferencing;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class CoreInferencingTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(CoreInferencingTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(ConstructorInferencingTest.class);
		suite.addTestSuite(DocumentationTest.class);
		suite.addTestSuite(DynamicTypeInferencingTest.class);
		suite.addTestSuite(FunctionInferencingTest.class);
		suite.addTestSuite(InferencingBugsTest.class);
		suite.addTestSuite(JSTypeUtilTest.class);
		suite.addTestSuite(ObjectInferencingTest.class);
		suite.addTestSuite(OperatorInferencingTest.class);
		suite.addTestSuite(PrimitiveInferencingTest.class);
		suite.addTestSuite(RecursiveInferencingTest.class);
		//$JUnit-END$
		return suite;
	}

}
