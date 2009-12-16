package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.RegexpRuleTest;
import com.aptana.editor.common.SequenceCharacterScannerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(SequenceCharacterScannerTest.class);
		suite.addTestSuite(RegexpRuleTest.class);
		// $JUnit-END$
		return suite;
	}

}
