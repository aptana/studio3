package com.aptana.red.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(CoreTests.suite());
		suite.addTest(UITests.suite());
		// $JUnit-END$
		return suite;
	}

	// Approach using reflection. Necessary if the host plugin (i.e. real plugin, not tests) doesn't have an
	// "Eclipse-ExtensibleAPI: true" entry in MANIFEST.MF
	// public static Test suite() throws ClassNotFoundException {
	// TestSuite suite = new TestSuite(
	// "Master test suite.");
	//
	// suite.addTest(getTest("com.mycompany.myplugin1.AllTests"));
	// suite.addTest(getTest("com.mycompany.myplugin2.AllTests"));
	// return suite;
	// }
	//
	// private static Test getTest(String suiteClassName) {
	// try {
	// Class clazz = Class.forName(suiteClassName);
	// Method suiteMethod = clazz.getMethod("suite", new Class[0]);
	// return (Test) suiteMethod.invoke(null, new Object[0]);
	// } catch (Exception e) {
	// throw new RuntimeException("Error", e);
	// }
	// }
}
