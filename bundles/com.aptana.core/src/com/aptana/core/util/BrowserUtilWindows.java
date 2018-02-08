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

import at.jta.Key;
import at.jta.RegistryErrorException;
import at.jta.Regor;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;

/**
 * Utilities for getting browser information on widows.
 * 
 * @author Fabio
 */
public class BrowserUtilWindows extends BrowserUtilNull
{

	/**
	 * Windows-only. Utility method to read the default value from a key in the registry.
	 */
	private static String readKeyValue(Regor regor, Key key, String path) throws RegistryErrorException
	{
		return readKeyValue(regor, key, path, "");// default value of key //$NON-NLS-1$
	}

	/**
	 * Windows-only. Utility method to read the default value from a key in the registry.
	 */
	private static String readKeyValue(Regor regor, Key key, String path, String valueName)
			throws RegistryErrorException
	{
		Key openKey = regor.openKey(key, path, Regor.KEY_READ);
		if (openKey != null)
		{
			try
			{
				byte buf[] = regor.readValue(openKey, valueName);
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

	/**
	 * This method tries to discover all the browsers installed in the computer.
	 */
	@Override
	public List<BrowserInfo> discoverInstalledBrowsers()
	{
		// Map Location -> info (remove duplicates from WOW6432Node).
		Map<String, BrowserInfo> browsers = new HashMap<String, BrowserInfo>();
		try
		{
			// On Windows, the available browsers should be found in the registry.
			// See: http://stackoverflow.com/questions/2370732/how-to-find-all-the-browsers-installed-on-a-machine
			Regor regor = new Regor();

			for (String path : new String[] { "Software\\Clients\\StartMenuInternet", //$NON-NLS-1$
					"Software\\WOW6432Node\\Clients\\StartMenuInternet" }) //$NON-NLS-1$ 
			{
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
										browserLocation = browserLocation.substring(1, browserLocation.length() - 1);
									}
									if (new File(browserLocation).exists())
									{
										browsers.put(browserLocation, new BrowserInfo(browserName, browserLocation));
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
		catch (Throwable e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return new ArrayList<BrowserInfo>(browsers.values());
	}

	@Override
	public String getBrowserVersion(BrowserInfo info)
	{
		String name = info.getName();
		if (StringUtil.isEmpty(name))
		{
			return null; // null expected when unknown
		}
		String lowerName = name.toLowerCase();
		if (lowerName.contains("opera")) //$NON-NLS-1$
		{
			return getOperaVersion();
		}
		if (lowerName.contains("internet explorer")) //$NON-NLS-1$
		{
			return getInternetExplorerVersion();
		}
		if (lowerName.contains("safari")) //$NON-NLS-1$
		{
			return getSafariVersion();
		}
		if (lowerName.contains("chrome")) //$NON-NLS-1$
		{
			return getChromeVersion();
		}
		if (lowerName.contains("firefox")) //$NON-NLS-1$
		{
			return getFirefoxVersion();
		}

		return null;
	}

	private String getVersionFrom(String[] paths)
	{
		return getVersionFrom(paths, ""); //$NON-NLS-1$
	}

	private String getVersionFrom(String[] paths, String keyValue)
	{
		return getVersionFrom(paths, keyValue, Regor.HKEY_LOCAL_MACHINE);
	}

	private String getVersionFrom(String[] paths, String keyValue, Key key)
	{
		try
		{
			Regor regor = new Regor();

			for (String path : paths)
			{
				String version = readKeyValue(regor, key, path, keyValue);
				if (!StringUtil.isEmpty(version))
				{
					return version;
				}
			}
		}
		catch (Throwable e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
	}

	private String getFirefoxVersion()
	{
		return getVersionFrom(new String[] { "Software\\Mozilla\\Mozilla Firefox\\", //$NON-NLS-1$
				"Software\\WOW6432Node\\Mozilla\\Mozilla Firefox\\" }); //$NON-NLS-1$
	}

	private String getInternetExplorerVersion()
	{
		return getVersionFrom(new String[] { "Software\\Microsoft\\Internet Explorer\\", //$NON-NLS-1$
				"Software\\WOW6432Node\\Microsoft\\Internet Explorer\\" }, "Version"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getSafariVersion()
	{
		return getVersionFrom(new String[] { "Software\\Apple Computer, Inc.\\Safari", //$NON-NLS-1$
				"Software\\WOW6432Node\\Apple Computer, Inc.\\Safari" }, "Version"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private String getChromeVersion()
	{
		String[] paths = new String[] { "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Google Chrome",
				"Software\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\Google Chrome" }; //$NON-NLS-1$ //$NON-NLS-2$
		String keyValue = "Version"; //$NON-NLS-1$ 
		String version = getVersionFrom(paths, keyValue, Regor.HKEY_CURRENT_USER);
		if (StringUtil.isEmpty(version))
		{
			return getVersionFrom(paths, keyValue);
		}
		return version;
	}

	/**
	 * Note that getting the opera version is a bit less straightforward because it adds the version number to the key,
	 * so, we have to list to get it.
	 */
	private String getOperaVersion()
	{
		try
		{
			Regor regor = new Regor();

			for (String path : new String[] { "Software\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\", //$NON-NLS-1$
					"Software\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\" }) //$NON-NLS-1$
			{
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
								if (s.toLowerCase().startsWith("opera")) { //$NON-NLS-1$
									// Additional validation: check publisher.
									String publisher = readKeyValue(regor, key, s, "Publisher"); //$NON-NLS-1$
									if (!StringUtil.isEmpty(publisher)
											&& publisher.toLowerCase().startsWith("opera software")) //$NON-NLS-1$
									{
										String version = readKeyValue(regor, key, s, "DisplayVersion"); //$NON-NLS-1$
										if (!StringUtil.isEmpty(version))
										{
											return version;
										}
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
		catch (Throwable e)
		{
			IdeLog.logError(CorePlugin.getDefault(), e);
		}
		return null;
	}

}
