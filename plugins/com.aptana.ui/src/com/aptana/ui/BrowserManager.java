/**
 * Aptana Studio
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.ui.internal.browser.BrowserDescriptorWorkingCopy;
import org.eclipse.ui.internal.browser.IBrowserDescriptor;

import com.aptana.core.IMap;
import com.aptana.core.util.BrowserUtil;
import com.aptana.core.util.CollectionsUtil;
import com.aptana.core.util.IBrowserUtil.BrowserInfo;

/**
 * This is the BrowserManager counterpart to be used in Aptana Studio instead of the Eclipse BrowserManager. It wraps up
 * details on how we want to work with browsers (i.e.: smarter discovery and version numbers).
 * 
 * @author Fabio
 */
@SuppressWarnings("restriction")
public class BrowserManager implements IBrowserProvider
{

	private static IBrowserProvider instance;

	public static synchronized IBrowserProvider getInstance()
	{
		if (instance == null)
		{
			instance = new BrowserManager();
		}
		return instance;
	}

	private BrowserManager()
	{
	}

	private String getRealPath(String loc)
	{
		if (loc == null)
		{
			return null; // default browser in eclipse
		}
		File file = new File(loc);
		String path;
		try
		{
			// Resolve links to see actual location
			path = file.getCanonicalPath();
		}
		catch (IOException e)
		{
			path = file.getAbsolutePath();
		}
		return path;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IBrowserProvider#searchMoreBrowsers()
	 */
	public Collection<BrowserInfo> searchMoreBrowsers()
	{
		org.eclipse.ui.internal.browser.BrowserManager eclipseBrowserManager = org.eclipse.ui.internal.browser.BrowserManager
				.getInstance();
		List<IBrowserDescriptor> webBrowsers = eclipseBrowserManager.getWebBrowsers();
		Set<String> currentBrowsers = new HashSet<String>();
		for (IBrowserDescriptor iBrowserDescriptor : webBrowsers)
		{
			String path = getRealPath(iBrowserDescriptor.getLocation());
			if (path == null)
			{
				continue;
			}
			currentBrowsers.add(path);
		}

		Collection<BrowserInfo> browsersFound = new ArrayList<BrowserInfo>();

		List<BrowserInfo> discoverInstalledBrowsers = BrowserUtil.discoverInstalledBrowsers();
		boolean needSave = false;
		for (BrowserInfo browserInfo : discoverInstalledBrowsers)
		{
			String browserLocation = getRealPath(browserInfo.getLocation());
			if (browserLocation != null && !currentBrowsers.contains(browserLocation))
			{
				currentBrowsers.add(browserLocation);
				BrowserDescriptorWorkingCopy workingCopy = new BrowserDescriptorWorkingCopy();
				workingCopy.setName(browserInfo.getName());
				workingCopy.setLocation(browserInfo.getLocation());
				workingCopy.save();
				browsersFound.add(browserInfo);
				needSave = true;
			}
		}
		if (needSave)
		{
			// forces a save on the new list of browsers
			eclipseBrowserManager.setCurrentWebBrowser(eclipseBrowserManager.getCurrentWebBrowser());
		}
		return browsersFound;
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IBrowserProvider#getWebBrowsers()
	 */
	public List<BrowserInfo> getWebBrowsers()
	{
		List<IBrowserDescriptor> webBrowsers = org.eclipse.ui.internal.browser.BrowserManager.getInstance()
				.getWebBrowsers();
		return CollectionsUtil.map(webBrowsers, new IMap<IBrowserDescriptor, BrowserInfo>()
		{
			public BrowserInfo map(IBrowserDescriptor browser)
			{
				return new BrowserInfo(browser.getName(), browser.getLocation());
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * @see com.aptana.ui.IBrowserProvider#getCurrentWebBrowser()
	 */
	public BrowserInfo getCurrentWebBrowser()
	{
		IBrowserDescriptor currentWebBrowser = org.eclipse.ui.internal.browser.BrowserManager.getInstance()
				.getCurrentWebBrowser();
		return new BrowserInfo(currentWebBrowser.getName(), currentWebBrowser.getLocation());
	}

	public String getBrowserVersion(BrowserInfo info)
	{
		return BrowserUtil.getBrowserVersion(info);
	}
}
