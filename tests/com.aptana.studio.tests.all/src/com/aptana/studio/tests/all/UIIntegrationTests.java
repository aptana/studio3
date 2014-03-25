/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.studio.tests.all;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.aptana.core.tests.StdErrLoggingSuite;
import com.aptana.editor.common.internal.commands.ExpandCollapseAllHandlerTest;
import com.aptana.editor.common.internal.commands.NextPreviousEditorHandlerTest;
import com.aptana.editor.html.contentassist.HTMLContentAssistProcessorIntegrationTest;
import com.aptana.editor.html.contentassist.HTMLNestedLanguageContentAssistTests;

@RunWith(StdErrLoggingSuite.class)
// @formatter:off
@Suite.SuiteClasses({ 
	HTMLContentAssistProcessorIntegrationTest.class,
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

}
