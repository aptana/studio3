/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

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

@RunWith(Suite.class)
// @formatter:off
@SuiteClasses({
	EditorCommonTests.class,
	PeerTests.class,
	RulesTests.class,
	SnippetsTests.class,
	ReconcilerTests.class,
	ScriptingCommandsTests.class,
	TextTests.class,
	UtilTests.class,
	ValidationTests.class,
	ViewerTests.class,
	ScriptingInputOutputTest.class,
	DocumentScopeManagerTest.class,
	ContentAssistTests.class,
	AllCompositeParserTests.class
})
// @formatter:on
public class AllTests
{
}
