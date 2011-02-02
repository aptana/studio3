/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.css.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.css.CSSCodeScannerPerformanceTest;
import com.aptana.editor.css.CSSParserPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance Tests for com.aptana.editor.css plugin");
		// $JUnit-BEGIN$
		suite.addTestSuite(CSSCodeScannerPerformanceTest.class);
		suite.addTestSuite(CSSParserPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
