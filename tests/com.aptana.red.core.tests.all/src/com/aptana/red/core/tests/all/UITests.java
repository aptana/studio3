package com.aptana.red.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.internal.commands.ExpandCollapseAllHandlerTest;
import com.aptana.editor.common.internal.commands.ExpandLevelHandlerTest;
import com.aptana.editor.common.internal.commands.NextPreviousEditorHandlerTest;

public class UITests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(UITests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.editor.common.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.css.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.erb.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.js.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.html.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.markdown.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.ruby.tests.AllTests.suite());
		suite.addTest(com.aptana.editor.sass.tests.AllTests.suite());
		// suite.addTest(com.aptana.editor.text.tests.AllTests.suite()); // TODO Add Tests for editor.text
		suite.addTest(com.aptana.editor.xml.tests.AllTests.suite());
		// suite.addTest(com.aptana.explorer.tests.AllTests.suite()); // TODO Add Tests for explorer
		suite.addTest(com.aptana.git.ui.AllTests.suite());
		// suite.addTest(com.aptana.scripting.ui.tests.AllTests.suite()); // TODO Add Tests for scripting.ui
		// suite.addTest(com.aptana.syncing.ui.tests.AllTests.suite()); // No syncing UI tests yet
		suite.addTest(com.aptana.theme.tests.AllTests.suite());
		suite.addTest(org.eclipse.tm.terminal.test.AllTests.suite());
		// $JUnit-END$
		
		// Now add special test cases which require to be run after all plugins are loaded (dependency inversion in
		// test)
		
		// require HTML editor to have outline contents to test common editor commands
		suite.addTestSuite(ExpandCollapseAllHandlerTest.class);
		suite.addTestSuite(ExpandLevelHandlerTest.class);
		suite.addTestSuite(NextPreviousEditorHandlerTest.class);
		return suite;
	}

}
