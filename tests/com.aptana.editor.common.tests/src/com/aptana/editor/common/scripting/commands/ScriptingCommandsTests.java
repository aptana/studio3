package com.aptana.editor.common.scripting.commands;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ScriptingCommandsTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(ScriptingCommandsTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(UtilitiesTest.class);
		suite.addTestSuite(TextEditorUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
