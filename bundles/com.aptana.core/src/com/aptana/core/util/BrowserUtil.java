/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.List;

import com.aptana.core.util.IBrowserUtil.BrowserInfo;

/**
 * Helper to get Browser information. Should usually NOT be directly accessed (com.aptana.ui.BrowserManager should be
 * used instead).
 * 
 * @author Fabio
 */
public class BrowserUtil
{

	private static IBrowserUtil browserUtil;

	private static synchronized void determineImpl()
	{
		if (browserUtil == null)
		{
			if (PlatformUtil.isWindows())
			{
				browserUtil = new BrowserUtilWindows();
			}
			else if (PlatformUtil.isMac())
			{
				browserUtil = new BrowserUtilMac();
			}
			else
			{
				browserUtil = new BrowserUtilNull();
			}
		}
	}

	/**
	 * @return a list with all the currently installed web browsers we're able to find (all the infos must be properly
	 *         filled with a non-null name and location at this point).
	 * @see com.aptana.ui.BrowserManager#searchMoreBrowsers() as usually this API should not be accessed directly.
	 */
	public static List<BrowserInfo> discoverInstalledBrowsers()
	{
		if (browserUtil == null)
		{
			determineImpl();
		}
		return browserUtil.discoverInstalledBrowsers();
	}

	/**
	 * @param info
	 *            the browser on which we want information.
	 * @return the version of the given browser. Returns null if the version could not be determined.
	 */
	public static String getBrowserVersion(BrowserInfo info)
	{
		if (browserUtil == null)
		{
			determineImpl();
		}
		return browserUtil.getBrowserVersion(info);
	}

}
