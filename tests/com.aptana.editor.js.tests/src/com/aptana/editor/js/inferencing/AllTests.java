/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.js.inferencing;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.editor.js.contentassist");
		// $JUnit-BEGIN$
		suite.addTestSuite(ConstructorInferencingTests.class);
		suite.addTestSuite(DocumentationTests.class);
		suite.addTestSuite(FunctionInferencingTests.class);
		suite.addTestSuite(ObjectInferencingTests.class);
		suite.addTestSuite(OperatorInferencingTests.class);
		suite.addTestSuite(PrimitiveInferencingTests.class);
		suite.addTestSuite(RecursiveInferencingTests.class);
		suite.addTestSuite(ScopeTests.class);
		// $JUnit-END$
		return suite;
	}
}
