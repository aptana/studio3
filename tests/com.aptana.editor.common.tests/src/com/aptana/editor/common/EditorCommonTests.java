package com.aptana.editor.common;

import junit.framework.Test;
import junit.framework.TestSuite;

public class EditorCommonTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(EditorCommonTests.class.getName());
		//$JUnit-BEGIN$
		suite.addTestSuite(SequenceCharacterScannerTest.class);
		suite.addTestSuite(TextUtilsTest.class);
		//$JUnit-END$
		return suite;
	}

}
