/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
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
		suite.addTest(com.aptana.filesystem.s3.tests.AllTests.suite()); // FIXME I think the way we do passwords causes us to have to run in UI!
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
		suite.addTest(com.aptana.editor.yaml.tests.AllTests.suite());
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
