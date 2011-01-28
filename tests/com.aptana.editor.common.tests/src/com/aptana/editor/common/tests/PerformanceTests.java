/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.editor.common.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.aptana.editor.common.internal.peer.PeerCharacterCloserPerfTest;
import com.aptana.editor.common.text.reconciler.RubyRegexpFolderPerformanceTest;

public class PerformanceTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite(PerformanceTests.class.getName());
		// $JUnit-BEGIN$
		suite.addTestSuite(PeerCharacterCloserPerfTest.class);
		suite.addTestSuite(RubyRegexpFolderPerformanceTest.class);
		// $JUnit-END$
		return suite;
	}

}
