/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import org.junit.Test;
import static org.junit.Assert.*;
import junit.framework.TestCase;

public class TimeZoneUtilTest
{

	@Test
	public void testGetCommonTimeZone()
	{
		assertEquals("", TimeZoneUtil.getCommonTimeZone(null));
		assertEquals("", TimeZoneUtil.getCommonTimeZone(new String[0]));

		assertEquals("EST", TimeZoneUtil.getCommonTimeZone(new String[] { "EST" }));
		assertEquals("CST", TimeZoneUtil.getCommonTimeZone(new String[] { "CST" }));
		assertEquals("MST", TimeZoneUtil.getCommonTimeZone(new String[] { "MST" }));
		assertEquals("PST", TimeZoneUtil.getCommonTimeZone(new String[] { "PST" }));
		assertEquals("GMT+8", TimeZoneUtil.getCommonTimeZone(new String[] { "GMT+8" }));
		assertEquals("Etc/GMT+8", TimeZoneUtil.getCommonTimeZone(new String[] { "Etc/GMT+8" }));
		assertEquals("ADT", TimeZoneUtil.getCommonTimeZone(new String[] { "ADT" }));
	}
}
