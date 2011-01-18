/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.sass.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.sass.SassCodeScannerPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Performance tests for com.aptana.editor.sass plugin"); //$NON-NLS-1$
		// $JUnit-BEGIN$
		suite.addTestSuite(SassCodeScannerPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}
}
