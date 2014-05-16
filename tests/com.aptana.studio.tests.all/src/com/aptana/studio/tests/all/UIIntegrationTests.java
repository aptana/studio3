/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.all;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.logging.IdeLog.StatusLevel;
import com.aptana.core.tests.StdErrLoggingSuite;
import com.aptana.core.util.EclipseUtil;
import com.aptana.editor.common.internal.commands.ExpandCollapseAllHandlerTest;
import com.aptana.editor.common.internal.commands.NextPreviousEditorHandlerTest;
import com.aptana.editor.common.scripting.ScriptingInputOutputIntegrationTest;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessorIntegrationTest;
import com.aptana.jira.core.JiraManagerIntegrationTest;

@RunWith(StdErrLoggingSuite.class)
// @formatter:off
@Suite.SuiteClasses({ 
	HTMLContentAssistProcessorIntegrationTest.class,
	ScriptingInputOutputIntegrationTest.class,
	com.aptana.editor.coffee.tests.IntegrationTests.class,
	com.aptana.editor.css.tests.IntegrationTests.class,
	com.aptana.editor.js.tests.IntegrationTests.class,
	JiraManagerIntegrationTest.class,
//	// Now add special test cases which require to be run after all plugins are loaded (dependency inversion in
//	// test)
//	// require HTML editor to have outline contents to test common editor commands
	ExpandCollapseAllHandlerTest.class,
//	// FIXME These tests are inconsistent and fail intermittently on test build
//	// ExpandLevelHandlerTest.class,
	NextPreviousEditorHandlerTest.class
})
// @formatter:on
public class UIIntegrationTests
{
	/**
	 * We turn logging level to INFO and turn on debugging for everything (basically setting logging to TRACE)
	 */
	@BeforeClass
	public static void turnUpLogging()
	{
		System.err.println("Turning logging to INFO");
		IdeLog.setCurrentSeverity(StatusLevel.INFO);
		System.err.println("Turning on all debug options");
		String[] currentOptions = EclipseUtil.getCurrentDebuggableComponents();
		EclipseUtil.setBundleDebugOptions(currentOptions, true);
		System.err.println("Turning on platform debugging flag");
		EclipseUtil.setPlatformDebugging(true);
	}
}
