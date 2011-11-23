/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.js.internal.text.JSFoldingComputerTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("All tests for com.aptana.editor.js");
		// $JUnit-BEGIN$
		suite.addTestSuite(JSFoldingComputerTest.class);
		suite.addTest(com.aptana.editor.js.AllTests.suite());
		suite.addTest(com.aptana.editor.js.contentassist.AllTests.suite());
		suite.addTest(com.aptana.editor.js.index.AllTests.suite());
		suite.addTest(com.aptana.editor.js.inferencing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.internal.build.AllTests.suite());
		suite.addTest(com.aptana.editor.js.internal.text.AllTests.suite());		
		suite.addTest(com.aptana.editor.js.outline.AllTests.suite());
		suite.addTest(com.aptana.editor.js.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.sdoc.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.vsdoc.parsing.AllTests.suite());
		suite.addTest(com.aptana.editor.js.text.AllTests.suite());
		// $JUnit-END$
		return suite;
	}
}
