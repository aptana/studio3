/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.osgi.framework.Version;

public class VersionUtilTest
{

	@Test
	public void testParseVersionQualifierSeparatedByHyphen()
	{
		assertVersion(3, 0, 24, "cr", VersionUtil.parseVersion("3.0.24-cr"));
	}

	@Test
	public void testParseVersionNull()
	{
		assertVersion(0, 0, 0, "", VersionUtil.parseVersion(null));
	}

	@Test
	public void testParseVersionEmpty()
	{
		assertVersion(0, 0, 0, "", VersionUtil.parseVersion(""));
	}

	@Test
	public void testIsEmptyNull()
	{
		assertTrue(VersionUtil.isEmpty(null));
	}

	@Test
	public void testIsEmptyVersionEmptyVersion()
	{
		assertTrue(VersionUtil.isEmpty(Version.emptyVersion));
	}

	@Test
	public void testIsEmpty()
	{
		assertFalse(VersionUtil.isEmpty(new Version(1, 0, 0)));
	}

	@Test
	public void testParseVersionWithMajorMinorMicro()
	{
		assertVersion(3, 0, 24, VersionUtil.parseVersion("3.0.24"));
		assertVersion(1, 12, 127, VersionUtil.parseVersion("1.12.127"));
	}

	@Test
	public void testParseVersionWithMajorMinorMicroQualifier()
	{
		assertVersion(3, 0, 0, "GA", VersionUtil.parseVersion("3.0.0.GA"));
		// Eclipse-style version #s
		assertVersion(1, 3, 0, "v20100106-170", VersionUtil.parseVersion("1.3.0.v20100106-170"));
		assertVersion(1, 2, 3, "1000a", VersionUtil.parseVersion("1.2.3.1000a"));
	}

	@Test
	public void testParseVersionWithMajorMinorMicroQualifierWithNoLeadingSeparator()
	{
		assertVersion(3, 0, 0, "GA", VersionUtil.parseVersion("3.0.0GA"));
		assertVersion(1, 2, 1, "b1", VersionUtil.parseVersion("1.2.1b1"));
	}

	@Test
	public void testParseVersionWithMajorMinor()
	{
		assertVersion(2, 0, 0, VersionUtil.parseVersion("2.0"));
		assertVersion(2, 10, 0, VersionUtil.parseVersion("2.10"));
	}

	@Test
	public void testParseVersionWithMajorMinorQualifierWithNoSeparator()
	{
		assertVersion(2, 1, 0, "a", VersionUtil.parseVersion("2.1a"));
	}

	private void assertVersion(int major, int minor, int micro, Version v)
	{
		assertVersion(major, minor, micro, null, v);
	}

	private void assertVersion(int major, int minor, int micro, String qualifier, Version v)
	{
		assertNotNull(v);
		assertEquals(major, v.getMajor());
		assertEquals(minor, v.getMinor());
		assertEquals(micro, v.getMicro());
		if (qualifier != null)
		{
			assertEquals(qualifier, v.getQualifier());
		}
	}

	@Test
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

		// Compare versions that contain cr tags in version identifiers
		assertTrue(VersionUtil.compareVersions("1.1.1-cr", "1.1.1", true, true) < 0);
		assertTrue(VersionUtil.compareVersions("1.12.11-cr", "1.12.10", true, true) > 0);
		assertTrue(VersionUtil.compareVersions("3.1.1-cr", "3.1.1.GA", true, true) < 0);
		assertTrue(VersionUtil.compareVersions("3.1.3-cr", "3.1.3-cr", true, true) == 0);
	}

	@Test
	public void testCompareVersionsNotStrict()
	{
		assertTrue(VersionUtil.compareVersions("2.0", "1.0", false) > 0);
		assertTrue(VersionUtil.compareVersions("2.10", "2.2", false) > 0);
		assertTrue(VersionUtil.compareVersions("2.1a", "2.1b", false) < 0);

		// Firebug-specific version #s
		assertTrue(VersionUtil.compareVersions("1.7X.0a1", "1.7X.0a1", false) == 0);
		assertTrue(VersionUtil.compareVersions("1.7X.0a1", "1.7X.0a2", false) < 0);
		assertTrue(VersionUtil.compareVersions("1.7X.0a2", "1.7X.0a1", false) > 0);
		assertTrue(VersionUtil.compareVersions("1.2.1b1", "1.2.1", false) > 0);
		assertTrue(VersionUtil.compareVersions("1.2.1", "1.2.1b1", false) < 0);

		assertTrue(VersionUtil.compareVersions("1.7X", "1.7X.0", false) == 0);

		// Eclipse-style version #s
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100106-170", "1.3.0.v20100106-170", false) == 0);
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100106-170", "1.3.0.v20100518-1140", false) < 0);
		assertTrue(VersionUtil.compareVersions("1.3.0.v20100518-1140", "1.3.0.v20100106-170", false) > 0);
		assertTrue(VersionUtil.compareVersions("v20100101-900", "v20100101-1200", false) > 0);

		assertTrue(VersionUtil.compareVersions("1.12.127", "1.12.82", false) > 0);
		assertTrue(VersionUtil.compareVersions("1.2.3.1000a", "1.2.3.1000b", false) < 0);
		assertTrue(VersionUtil.compareVersions("1.12", "1.12", false) == 0);
		assertTrue(VersionUtil.compareVersions("1.12", "1.12.0", false) == 0);
		assertTrue(VersionUtil.compareVersions("1.12.0", "1.12", false) == 0);
	}

	/**
	 * Version ranges tests.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testVersionRanges() throws Exception
	{
		assertTrue("Expected empty version values to be compatible",
				VersionUtil.isCompatibleVersions(new String[] {}, new String[] {}));
		assertTrue("Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0" }, new String[] { "1\\.0" }));
		assertTrue("Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0" }, new String[] { "[1.0, 2.0)" }));
		assertTrue("Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0", "2.0" }, new String[] { "[1.0, 2.0)" }));
		assertTrue("Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0", "2.0" }, new String[] { "[1.0, 2.0]" }));
		assertTrue("Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0", "2.0", "2.1" }, new String[] { "[1.0, 2.0]" }));
		assertTrue("Expected compatible versions", VersionUtil.isCompatibleVersions(new String[] { "1.0.1", "2.0",
				"2.1" }, new String[] { "[1.0.0, 2.0]" }));
		assertTrue(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0", "2.0", "2.1" }, new String[] { "[1.0, 2.0)",
						"2\\.1" }));
		assertTrue(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "[2.1, 2.2]" }));
		assertTrue(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "[2.1, 2.2)" }));
		assertTrue(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "2\\.1", "2\\.2" }));
		assertTrue(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "Android\\s*2\\.1.*",
						"Android\\s*2\\.2.*" }));

		assertFalse(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "Android\\s*2\\.1.*",
						"Android\\s*2\\.2.*", "Android\\s*2\\.3.*" }));
		assertFalse(
				"Expected compatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "Android 2.1-update1",
						"Google APIs Android 2.1-update1", "Android 2.2" }, new String[] { "2\\.3", "2\\.2" }));
		assertFalse(
				"Expected incompatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0", "2.0", "2.1" }, new String[] { "[1.0, 2.1]",
						"2\\.2" }));
		assertFalse("Expected incompatible versions", VersionUtil.isCompatibleVersions(new String[] { "1.0.1", "2.0.0",
				"2.1.0" }, new String[] { "[1.0.2, 2.0.0)" }));
		assertFalse("Expected incompatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0" }, new String[] { "[1.0, 2.0)", "3\\.0" }));
		assertFalse("Expected incompatible versions",
				VersionUtil.isCompatibleVersions(new String[] { "1.0" }, new String[] { "(1.0, 2.0)" }));
	}

	@Test
	public void testVersionsWithHyphen()
	{
		assertTrue(VersionUtil.compareVersionsWithHyphen("1-rc", "1-rc2") < 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("1-rc2", "1-rc3") < 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("1-rc2", "1-rc2") == 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("1-rc2", "1") < 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("2-rc2", "1") > 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("1-rc", "0-rc2") > 0);
		assertTrue(VersionUtil.compareVersionsWithHyphen("1", "0-rc3") > 0);
	}

	@Test
	public void testParsingMaxVersion() throws Exception
	{
		assertEquals("24", VersionUtil.parseMax(">=20.x <=24"));
		assertEquals("24.0", VersionUtil.parseMax(">=20 <24.x"));
		assertEquals("24.0", VersionUtil.parseMax("<=24.x"));
		assertEquals("24.9", VersionUtil.parseMax("<24.9"));
		assertEquals(null, VersionUtil.parseMax(">=24"));
	}

	@Test
	public void testParsingMinVersion() throws Exception
	{
		assertEquals("20.0", VersionUtil.parseMin(">=20.x <=24"));
		assertEquals("20", VersionUtil.parseMin(">20 <=24.x"));
		assertEquals("24.0", VersionUtil.parseMin(">=24.x"));
		assertEquals("24.9", VersionUtil.parseMin(">24.9"));
		assertEquals(null, VersionUtil.parseMin("<=24"));
	}
	
	@Test
	public void testisMinimumCompatibleVersions()
	{
		assertFalse(VersionUtil.isMinimumCompatibleVersions(new String[]{ "1.0", "2.0" }, new String[]{ "[5.0, 9.0]" }));
		assertTrue(VersionUtil.isMinimumCompatibleVersions(new String[]{ "5.0", "2.0" }, new String[]{ "[5.0, 9.0]" }));
		assertTrue(VersionUtil.isMinimumCompatibleVersions(new String[]{ "5.0", "2.0" }, new String[]{ "[5.0]" }));
		assertTrue(VersionUtil.isMinimumCompatibleVersions(new String[]{ "9.0"}, new String[]{ "[5.0]" }));
		assertTrue(VersionUtil.isMinimumCompatibleVersions(new String[]{ "9.0"}, new String[]{  }));
		assertFalse(VersionUtil.isMinimumCompatibleVersions(new String[]{ "8.4"}, new String[]{ "[9.0]" }));
		assertFalse(VersionUtil.isMinimumCompatibleVersions(new String[]{ "8.4", "9.0"}, new String[]{ "[5.0, 8.3]" }));
		assertFalse(VersionUtil.isMinimumCompatibleVersions(new String[]{ "9.0"}, new String[]{ "[5.0, 9.0)" }));
	}
}
