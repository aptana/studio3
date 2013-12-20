/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.aptana.core.util.IBrowserUtil.BrowserInfo;

public class BrowserUtilTest
{

	@Test
	public void testFindBrowser() throws Exception
	{
		List<BrowserInfo> browsers = BrowserUtil.discoverInstalledBrowsers();
		if (PlatformUtil.isWindows())
		{
			for (BrowserInfo info : browsers)
			{
				if (info.getName().equals("Internet Explorer"))
				{
					return;
				}
			}
			fail("Could not find Internet Explorer on windows platform!");
		}
	}

	/**
	 * Test that prints what's found in the current machine (used for debugging purposes as it may be hard to predict
	 * the environment used to run the tests).
	 */
	@Test
	public void testFindBrowserManual() throws Exception
	{
		List<BrowserInfo> browsers = BrowserUtil.discoverInstalledBrowsers();
		for (BrowserInfo info : browsers)
		{
			System.out.println(String.format("%s (%s)  - %s", info.getName(), BrowserUtil.getBrowserVersion(info),
					info.getLocation()));
		}
	}

}
