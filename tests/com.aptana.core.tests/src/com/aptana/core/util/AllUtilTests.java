/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import junit.framework.Test;
import junit.framework.TestResult;
import junit.framework.TestSuite;

public class AllUtilTests
{

	public static Test suite()
	{
		TestSuite suite = new TestSuite("Test for com.aptana.core.util")
		{
			@Override
			public void runTest(Test test, TestResult result)
			{
				System.err.println("Running test: " + test.toString());
				super.runTest(test, result);
			}
		};
		// $JUnit-BEGIN$
		suite.addTestSuite(ArrayUtilTest.class);
		suite.addTestSuite(BrowserUtilTest.class);
		suite.addTestSuite(ClassUtilTest.class);
		suite.addTestSuite(CollectionsUtilTest.class);
		suite.addTestSuite(EclipseUtilTest.class);
		suite.addTestSuite(ExecutableUtilTest.class);
		suite.addTestSuite(ExpiringMapTests.class);
		suite.addTestSuite(FileUtilTest.class);
		suite.addTestSuite(FirefoxUtilTest.class);
		suite.addTestSuite(ImmutableTupleNTest.class);
		suite.addTestSuite(InputStreamGobblerTest.class);
		suite.addTestSuite(IOUtilTest.class);
		suite.addTestSuite(ObjectUtilTest.class);
		suite.addTestSuite(OutputStreamThreadTest.class);
		suite.addTestSuite(PlatformUtilTest.class);
		suite.addTestSuite(ProcessStatusTest.class);
		suite.addTestSuite(ProcessUtilTest.class);
		suite.addTestSuite(ProgressMonitorInterrupterTest.class);
		suite.addTestSuite(RegexUtilTest.class);
		suite.addTestSuite(ResourceUtilTest.class);
		suite.addTestSuite(SocketUtilTest.class);
		suite.addTestSuite(SourcePrinterTest.class);
		suite.addTestSuite(StreamUtilTest.class);
		suite.addTestSuite(StringUtilTest.class);
		suite.addTestSuite(TimeZoneUtilTest.class);
		suite.addTestSuite(URLEncoderTest.class);
		suite.addTestSuite(URLUtilTest.class);
		suite.addTestSuite(VersionUtilTest.class);
		suite.addTestSuite(WriterOutputStreamTest.class);
		suite.addTestSuite(ZipUtilTest.class);
		suite.addTestSuite(StatusCollectorTest.class);
		suite.addTestSuite(PatternReplacerTest.class);
		// $JUnit-END$
		return suite;
	}
}
