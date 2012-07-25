/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.util.List;

public interface IBrowserUtil
{

	public static class BrowserInfo
	{
		private final String browserName;
		private final String browserLocation;

		public BrowserInfo(String browserName, String browserLocation)
		{
			this.browserName = browserName;
			this.browserLocation = browserLocation;
		}

		@Override
		public String toString()
		{
			return StringUtil.join("", "BrowserInfo[", browserName, ", ", browserLocation, "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}

		public String getName()
		{
			return browserName;
		}

		public String getLocation()
		{
			return browserLocation;
		}
	}

	/**
	 * @return a list with the browser information for the current user.
	 */
	List<BrowserInfo> discoverInstalledBrowsers();

	/**
	 * @param info
	 *            the browser on which we want information.
	 * @return the version of the given browser. Returns null if the version could not be determined.
	 */
	String getBrowserVersion(BrowserInfo info);

}
