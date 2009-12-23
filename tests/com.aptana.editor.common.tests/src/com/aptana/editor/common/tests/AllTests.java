package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.EditorCommonTests;
import com.aptana.editor.common.internal.peer.PeerTests;
import com.aptana.editor.common.text.rules.RulesTests;
import com.aptana.editor.common.theme.ThemeTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(EditorCommonTests.suite());
		suite.addTest(PeerTests.suite());
		suite.addTest(RulesTests.suite());
		suite.addTest(ThemeTests.suite());
		// $JUnit-END$
		return suite;
	}

}
