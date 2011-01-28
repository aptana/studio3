/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.dtd;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.dtd.parsing.DTDParserTests;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.dtd");
		//$JUnit-BEGIN$
		suite.addTestSuite(DTDSourceScannerTests.class);
		suite.addTestSuite(DTDParserTests.class);
		//$JUnit-END$
		return suite;
	}

}
