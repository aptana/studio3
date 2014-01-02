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

@RunWith(Suite.class)
// @formatter:off
@Suite.SuiteClasses({ 
	com.aptana.browser.tests.AllTests.class,
//	com.aptana.console.tests.AllTests.class,
	
//	com.aptana.editor.common.tests.AllTests.class,
//	com.aptana.editor.css.tests.AllTests.class,
//	com.aptana.editor.css.formatter.tests.AllTests.class,
//	com.aptana.editor.dtd.AllTests.class,
//	com.aptana.editor.html.tests.AllTests.class,
//	com.aptana.editor.html.formatter.tests.AllTests.class,
//	com.aptana.editor.js.tests.AllTests.class,
//	com.aptana.editor.js.formatter.tests.AllTests.class,
//	com.aptana.editor.json.tests.AllTests.class,
//	com.aptana.editor.xml.tests.AllTests.class,
//	com.aptana.editor.xml.formatter.tests.AllTests.class,
//	com.aptana.deploy.ftp.tests.AllTests.class,
//	// com.aptana.editor.text.tests.AllTests.class, // TODO Add Tests for editor.text
//	// com.aptana.explorer.tests.AllTests.class, // TODO Add Tests for explorer
//	// com.aptana.filesystem.s3.tests.AllTests.class // FIXME I think the way we do passwords causes us to have to run s3 tests in the UI!
//	com.aptana.git.ui.AllTests.class,
//	com.aptana.portal.ui.tests.AllTests.class,
//	// com.aptana.scripting.ui.tests.AllTests.class, // TODO Add Tests for scripting.ui
//	// com.aptana.syncing.ui.tests.AllTests.class, // FIXME Re-enable when we have ftp server set back up...
//	com.aptana.theme.tests.AllTests.class,
//	org.eclipse.tm.terminal.test.AllTests.class,
//	
//	// Now add special test cases which require to be run after all plugins are loaded (dependency inversion in
//	// test)
//	// require HTML editor to have outline contents to test common editor commands
//	ExpandCollapseAllHandlerTest.class,
//	// FIXME These tests are inconsistent and fail intermittently on test build
//	// ExpandLevelHandlerTest.class,
//	NextPreviousEditorHandlerTest.class
})
// @formatter:on
public class UITests
{

}
