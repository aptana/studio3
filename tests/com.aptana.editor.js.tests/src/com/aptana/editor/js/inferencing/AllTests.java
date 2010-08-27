package com.aptana.editor.js.inferencing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.contentassist");
		// $JUnit-BEGIN$
		suite.addTestSuite(ConstructorInferencingTests.class);
		suite.addTestSuite(DocumentationTests.class);
		suite.addTestSuite(FunctionInferencingTests.class);
		suite.addTestSuite(ObjectInferencingTests.class);
		suite.addTestSuite(OperatorInferencingTests.class);
		suite.addTestSuite(PrimitiveInferencingTests.class);
		suite.addTestSuite(RecursiveInferencingTests.class);
		suite.addTestSuite(ScopeTests.class);
		// $JUnit-END$
		return suite;
	}
}
