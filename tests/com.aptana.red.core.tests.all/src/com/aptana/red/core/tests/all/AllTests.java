package com.aptana.red.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		//$JUnit-BEGIN$
		// Core
		suite.addTest(com.aptana.git.core.tests.AllTests.suite());
		suite.addTest(com.aptana.util.tests.AllTests.suite());
		suite.addTest(com.aptana.scripting.tests.AllTests.suite());
		// UI
		suite.addTest(com.aptana.git.ui.AllTests.suite());
		suite.addTest(com.aptana.editor.common.tests.AllTests.suite());
//		suite.addTest(com.aptana.editor.text.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.xml.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.css.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.js.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.html.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.ruby.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.sass.tests.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

	// Approach using reflection. Necessary if the host plugin (i.e. real plugin, not tests) doesn't have an "Eclipse-ExtensibleAPI: true" entry in MANIFEST.MF
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
