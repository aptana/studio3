/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import com.aptana.editor.common.EditorCommonTests;
import com.aptana.editor.common.contentassist.ContentAssistTests;
import com.aptana.editor.common.internal.peer.PeerTests;
import com.aptana.editor.common.internal.scripting.DocumentScopeManagerTest;
import com.aptana.editor.common.parsing.AllCompositeParserTests;
import com.aptana.editor.common.scripting.ScriptingInputOutputTest;
import com.aptana.editor.common.scripting.commands.ScriptingCommandsTests;
import com.aptana.editor.common.scripting.snippets.SnippetsTests;
import com.aptana.editor.common.text.TextTests;
import com.aptana.editor.common.text.reconciler.ReconcilerTests;
import com.aptana.editor.common.text.rules.RulesTests;
import com.aptana.editor.common.util.UtilTests;
import com.aptana.editor.common.validation.ValidationTests;
import com.aptana.editor.common.viewer.ViewerTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName())
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTest(EditorCommonTests.suite());
		suite.addTest(PeerTests.suite());
		suite.addTest(RulesTests.suite());
		suite.addTest(SnippetsTests.suite());
		suite.addTest(ReconcilerTests.suite());
		suite.addTest(ScriptingCommandsTests.suite());
		suite.addTest(TextTests.suite());
		suite.addTest(UtilTests.suite());
		suite.addTest(ValidationTests.suite());
		suite.addTest(ViewerTests.suite());
		suite.addTestSuite(ScriptingInputOutputTest.class);
		suite.addTestSuite(DocumentScopeManagerTest.class);
		suite.addTest(ContentAssistTests.suite());
		suite.addTest(AllCompositeParserTests.suite());
		// $JUnit-END$
		return suite;
	}

}
