/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import junit.framework.TestCase;

public class VersionUtilTest extends TestCase
{

	public void testCompareVersions()
	{
		assertTrue(VersionUtil.compareVersions("2.0", "1.0") > 0);
		assertTrue(VersionUtil.compareVersions("2.10", "2.2") > 0);
		assertTrue(VersionUtil.compareVersions("2.1a", "2.1b") < 0);

		// Firebug-specific version #s
		assertTrue(VersionUtil.compareVersions("1.7X.0a1", "1.7X.0a1") == 0);
		assertTrue(VersionUtil.compareVersions("1.7X.0a1", "1.7X.0a2") < 0);
		assertTrue(VersionUtil.compareVersions("1.7X.0a2", "1.7X.0a1") > 0);
		assertTrue(VersionUtil.compareVersions("1.2.1b1", "1.2.1") > 0);
		assertTrue(VersionUtil.compareVersions("1.2.1", "1.2.1b1") < 0);

		// Eclipse-style version #s
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100106-170", "1.3.0.v20100106-170") == 0);
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100106-170", "1.3.0.v20100518-1140") < 0);
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100518-1140", "1.3.0.v20100106-170") > 0);
		assertTrue(VersionUtil.compareVersions("v20100101-900", "v20100101-1200") > 0);
		
		assertTrue(VersionUtil.compareVersions("1.12.127", "1.12.82") > 0);
		assertTrue(VersionUtil.compareVersions("1.2.3.1000a", "1.2.3.1000b") < 0);
		assertTrue(VersionUtil.compareVersions("1.12", "1.12") == 0);
		assertTrue(VersionUtil.compareVersions("1.12", "1.12.0") < 0);
		assertTrue(VersionUtil.compareVersions("1.12.0", "1.12") > 0);
	}
}
