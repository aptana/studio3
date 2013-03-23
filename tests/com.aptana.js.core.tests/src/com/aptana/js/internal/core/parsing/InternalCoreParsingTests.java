/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.js.internal.core.parsing;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author klindsey
 *
 */
public class InternalCoreParsingTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Tests for com.aptana.js.internal.core.parsing");
		//$JUnit-BEGIN$
		suite.addTestSuite(VSDocNodeAttachmentTest.class);
		suite.addTestSuite(VSDocReaderTest.class);
		//$JUnit-END$
		return suite;
	}

}
