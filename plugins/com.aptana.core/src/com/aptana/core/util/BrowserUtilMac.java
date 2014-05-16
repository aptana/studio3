/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.core.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.plist.PListParserFactory;

public class BrowserUtilMac extends BrowserUtilNull
{

	/**
	 * Locations of the info.plist for browsers.
	 */
	private static final IPath CHROME_PLIST = Path
			.fromPortableString("/Applications/Google Chrome.app/Contents/Info.plist"); //$NON-NLS-1$
	private static final IPath SAFARI_PLIST = Path.fromPortableString("/Applications/Safari.app/Contents/Info.plist"); //$NON-NLS-1$
	private static final IPath OPERA_PLIST = Path.fromPortableString("/Applications/Opera.app/Contents/Info.plist"); //$NON-NLS-1$

	/**
	 * The key used to store teh version number in PList files.
	 */
	private static final String PLIST_VERSION_KEY = "CFBundleShortVersionString"; //$NON-NLS-1$

	// @formatter:off
	private static final Map<String, String> MAC_BROWSER_LOCATIONS = CollectionsUtil.newMap(
			"Chrome", "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome", //$NON-NLS-1$ //$NON-NLS-2$
			"Safari", "/Applications/Safari.app/Contents/MacOS/Safari", //$NON-NLS-1$ //$NON-NLS-2$
			"Firefox", "/Applications/Firefox.app/Contents/MacOS/firefox-bin", //$NON-NLS-1$ //$NON-NLS-2$
			"Opera", "/Applications/Opera.app/Contents/MacOS/Opera"); //$NON-NLS-1$ //$NON-NLS-2$
	// @formatter:on

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
			return getFirefoxVersion(info);
		}

		return null;
	}

	private String getFirefoxVersion(BrowserInfo info)
	{
		IStatus status = new ProcessRunner().runInBackground(Path.ROOT, info.getLocation(), "-v"); //$NON-NLS-1$
		if (status == null || !status.isOK())
		{
			return null;
		}
		String output = status.getMessage();
		List<String> parts = StringUtil.split(output, ' ');
		return parts.get(parts.size() - 1);
	}

	private String getOperaVersion()
	{
		return getPlistKeyValue(OPERA_PLIST, PLIST_VERSION_KEY);
	}

	private String getSafariVersion()
	{
		return getPlistKeyValue(SAFARI_PLIST, PLIST_VERSION_KEY);
	}

	private String getChromeVersion()
	{
		return getPlistKeyValue(CHROME_PLIST, PLIST_VERSION_KEY);
	}

	private String getPlistKeyValue(IPath plistPath, String key)
	{
		try
		{
			Map<String, Object> dict = PListParserFactory.parse(plistPath.toFile());
			return (String) dict.get(key);
		}
		catch (IOException e)
		{
			// TODO log an error?
			return null;
		}
	}
}
