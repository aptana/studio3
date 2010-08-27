package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.EditorCommonTests;
import com.aptana.editor.common.internal.peer.PeerTests;
import com.aptana.editor.common.scripting.ScriptingInputOutputTest;
import com.aptana.editor.common.scripting.commands.ScriptingCommandsTests;
import com.aptana.editor.common.scripting.snippets.SnippetsTests;
import com.aptana.editor.common.text.RubyRegexpAutoIndentStrategyTest;
import com.aptana.editor.common.text.reconciler.ReconcilerTests;
import com.aptana.editor.common.text.rules.RulesTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(EditorCommonTests.suite());
		suite.addTest(PeerTests.suite());
		suite.addTest(RulesTests.suite());
		suite.addTest(SnippetsTests.suite());
		suite.addTest(ReconcilerTests.suite());
		suite.addTest(ScriptingCommandsTests.suite());
		suite.addTestSuite(ScriptingInputOutputTest.class);
		suite.addTestSuite(RubyRegexpAutoIndentStrategyTest.class);
		// $JUnit-END$
		return suite;
	}

}
