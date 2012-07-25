/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

public class BrowserUtilMac extends BrowserUtilNull
{

	private static final Map<String, String> MAC_BROWSER_LOCATIONS;
	static
	{
		MAC_BROWSER_LOCATIONS = new HashMap<String, String>();
		MAC_BROWSER_LOCATIONS.put("Chrome", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Safari", "/Applications/Safari.app/Contents/MacOS/Safari"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Firefox", "/Applications/Firefox.app/Contents/MacOS/firefox-bin"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Opera", "/Applications/Opera.app/Contents/MacOS/Opera"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.core.util.IBrowserUtil#discoverInstalledBrowsers()
	 */
	@Override
	public List<BrowserInfo> discoverInstalledBrowsers()
	{
		List<BrowserInfo> browsers = new ArrayList<BrowserInfo>();
		try
		{
			Set<String> browserNames = MAC_BROWSER_LOCATIONS.keySet();
			for (String name : browserNames)
			{
				String location = MAC_BROWSER_LOCATIONS.get(name);
				if (new File(location).exists())
				{
					browsers.add(new BrowserInfo(name, location));
				}
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return browsers;
	}

}
