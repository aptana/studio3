/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.red.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.internal.commands.ExpandCollapseAllHandlerTest;
import com.aptana.editor.common.internal.commands.NextPreviousEditorHandlerTest;

public class UITests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(UITests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.filesystem.s3.tests.AllTests.suite()); // FIXME I think the way we do passwords causes
																		// us to have to run in UI!
		suite.addTest(com.aptana.editor.common.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.css.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.css.formatter.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.dtd.AllTests.suite());
		suite.addTest(com.aptana.editor.html.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.html.formatter.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.idl.AllTests.suite());
		suite.addTest(com.aptana.editor.js.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.js.formatter.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.json.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.markdown.tests.AllTests.suite());
		// suite.addTest(com.aptana.editor.text.tests.AllTests.suite()); // TODO Add Tests for editor.text
		suite.addTest(com.aptana.editor.xml.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.xml.formatter.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.yaml.tests.AllTests.suite());
		// suite.addTest(com.aptana.explorer.tests.AllTests.suite()); // TODO Add Tests for explorer
		suite.addTest(com.aptana.git.ui.AllTests.suite());
		// suite.addTest(com.aptana.scripting.ui.tests.AllTests.suite()); // TODO Add Tests for scripting.ui
		suite.addTest(com.aptana.syncing.ui.tests.AllTests.suite());
		suite.addTest(com.aptana.theme.tests.AllTests.suite());
		suite.addTest(org.eclipse.tm.terminal.test.AllTests.suite());
		// $JUnit-END$

		// Now add special test cases which require to be run after all plugins are loaded (dependency inversion in
		// test)

		// require HTML editor to have outline contents to test common editor commands
		suite.addTestSuite(ExpandCollapseAllHandlerTest.class);
		// FIXME These tests are inconsistent and fail intermittently on test build
		// suite.addTestSuite(ExpandLevelHandlerTest.class);
		suite.addTestSuite(NextPreviousEditorHandlerTest.class);
		return suite;
	}

}
