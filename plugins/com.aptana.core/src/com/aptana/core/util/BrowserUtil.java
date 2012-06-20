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

import at.jta.Key;
import at.jta.RegistryErrorException;
import at.jta.Regor;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

public class BrowserUtil
{

	public static class BrowserInfo
	{
		public final String browserName;
		public final String browserLocation;

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
	}

	private static final Map<String, String> MAC_BROWSER_LOCATIONS;
	static
	{
		MAC_BROWSER_LOCATIONS = new HashMap<String, String>();
		MAC_BROWSER_LOCATIONS.put("Chrome", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Safari", "/Applications/Safari.app/Contents/MacOS/Safari"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Firefox", "/Applications/Firefox.app/Contents/MacOS/firefox-bin"); //$NON-NLS-1$ //$NON-NLS-2$
		MAC_BROWSER_LOCATIONS.put("Opera", "/Applications/Opera.app/Contents/MacOS/Opera"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This method tries to discover all the browsers installed in the computer.
	 */
	public static List<BrowserInfo> discoverInstalledBrowsers()
	{
		List<BrowserInfo> browsers = new ArrayList<BrowserInfo>();
		try
		{
			if (PlatformUtil.isWindows())
			{
				// On Windows, the available browsers should be found in the registry.
				// See: http://stackoverflow.com/questions/2370732/how-to-find-all-the-browsers-installed-on-a-machine
				Regor regor = new Regor();

				for (String path : new String[] {
						"Software\\Clients\\StartMenuInternet", "Software\\WOW6432Node\\Clients\\StartMenuInternet" }) { //$NON-NLS-1$ //$NON-NLS-2$
					Key key = regor.openKey(Regor.HKEY_LOCAL_MACHINE, path, Regor.KEY_READ);
					if (key != null)
					{
						try
						{
							@SuppressWarnings("unchecked")
							List<String> keys = regor.listKeys(key);
							if (keys != null)
							{
								for (String s : keys)
								{
									String browserName = readKeyValue(regor, key, s);
									String browserLocation = readKeyValue(regor, key, s + "\\shell\\open\\command"); //$NON-NLS-1$

									if (browserName != null && browserLocation != null)
									{
										// Only add it if it really exists.
										if (browserLocation.startsWith("\"") && browserLocation.endsWith("\"")) //$NON-NLS-1$ //$NON-NLS-2$
										{
											browserLocation = browserLocation
													.substring(1, browserLocation.length() - 1);
										}
										if (new File(browserLocation).exists())
										{
											browsers.add(new BrowserInfo(browserName, browserLocation));
										}
									}
								}
							}
						}
						finally
						{
							regor.closeKey(key);
						}
					}
				}
			}
			else if (PlatformUtil.isMac())
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
			// TODO: Handle other platforms (ongoing work).
		}
		catch (Throwable e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return browsers;
	}

	/**
	 * Windows-only. Utility method to read the default value from a key in the registry.
	 */
	private static String readKeyValue(Regor regor, Key key, String path) throws RegistryErrorException
	{
		Key openKey = regor.openKey(key, path, Regor.KEY_READ);
		if (openKey != null)
		{
			try
			{
				byte buf[] = regor.readValue(openKey, ""); // default value of key //$NON-NLS-1$
				if (buf != null)
				{
					return Regor.parseValue(buf);
				}
			}
			finally
			{
				regor.closeKey(openKey);
			}

		}
		return null;
	}

}
