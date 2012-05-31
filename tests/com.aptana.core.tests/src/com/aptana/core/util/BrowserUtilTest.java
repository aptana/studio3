/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.List;

import com.aptana.core.util.BrowserUtil.BrowserInfo;

import junit.framework.TestCase;

public class BrowserUtilTest extends TestCase
{

	public void testFindBrowser() throws Exception
	{
		List<BrowserInfo> browsers = BrowserUtil.discoverInstalledBrowsers();
		if (PlatformUtil.isWindows())
		{
			for (BrowserInfo info : browsers)
			{
				if (info.browserName.equals("Internet Explorer"))
				{
					return;
				}
			}
			fail("Could not find Internet Explorer on windows platform!");
		}

	}
}
