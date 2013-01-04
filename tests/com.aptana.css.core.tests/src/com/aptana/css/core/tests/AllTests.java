/**
 * Aptana Studio
 * Copyright (c) 2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.css.core.tests;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.aptana.css.core.index.CSSIndexTests;
import com.aptana.css.core.internal.build.CSSBuildParticipantsTests;
import com.aptana.css.core.parsing.CSSParsingTests;

public class AllTests extends TestCase
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(AllTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTest(CSSParsingTests.suite());
		suite.addTest(CSSIndexTests.suite());
		suite.addTest(CSSBuildParticipantsTests.suite());
		// $JUnit-END$
		return suite;
	}
}
