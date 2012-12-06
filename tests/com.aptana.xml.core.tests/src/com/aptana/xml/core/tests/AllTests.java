/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.xml.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.xml.core.model.DTDTransformationTests;
import com.aptana.xml.core.parsing.XMLParserTest;

public class AllTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(XMLParserTest.class);
		suite.addTestSuite(DTDTransformationTests.class);
		// $JUnit-END$
		return suite;
	}

}
