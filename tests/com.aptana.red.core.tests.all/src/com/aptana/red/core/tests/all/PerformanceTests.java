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

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(com.aptana.red.core.tests.startup.AllTests.suite());
		suite.addTest(com.aptana.editor.common.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.css.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.js.tests.PerformanceTests.suite());
		suite.addTest(com.aptana.editor.html.tests.PerformanceTests.suite());
		// $JUnit-END$
		return suite;
	}
}
