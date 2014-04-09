/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */

package com.aptana.ui.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IProcessRunner;
import com.aptana.core.util.PlatformUtil;
import com.aptana.core.util.ProcessRunner;
import com.aptana.core.util.StringUtil;
import com.aptana.ui.UIPlugin;

/**
 * @author Max Stepanov
 */
public final class WorkbenchBrowserUtil
{

	/**
	 * 
	 */
	private WorkbenchBrowserUtil()
	{
	}

	public static void launchExternalBrowser(String url)
	{
		try
		{
			launchExternalBrowser(new URL(url), null);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
	}

	public static IWebBrowser launchExternalBrowser(String url, String browserId)
	{
		try
		{
			return launchExternalBrowser(new URL(url), browserId);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
		return null;
	}

	private static IWebBrowser launchExternalBrowser(URL url, String browserId)
	{
		IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();
		if (browserId != null)
		{
			try
			{
				IWebBrowser webBrowser = support.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL, browserId, null,
						null);
				if (webBrowser != null)
				{
					webBrowser.openURL(url);
					return webBrowser;
				}
			}
			catch (PartInitException e)
			{
				IdeLog.logError(UIPlugin.getDefault(), e);
			}
		}
		try
		{
			IWebBrowser webBrowser = support.getExternalBrowser();
			webBrowser.openURL(url);
			return webBrowser;
		}
		catch (Exception e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
			launchBrowserByCommand(url);
		}
		return null;
	}

	/**
	 * If we try to open URLs in the splash (before the platform loads), we typically fail and need a fallback mechanism
	 * to open URLs externally.
	 * 
	 * @param url
	 */
	@SuppressWarnings("nls")
	public static void launchBrowserByCommand(URL url)
	{
		IProcessRunner runner = new ProcessRunner();
		// Can we fall back to running a command to load the URL?
		if (PlatformUtil.isMac())
		{
			runner.runInBackground("open", url.toString());
		}
		else if (PlatformUtil.isWindows())
		{
			// Windows
			IStatus result = new ProcessRunner().runInBackground("reg", "query",
					"HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
			String output = result.getMessage();
			output = output.trim();
			int index = output.indexOf("REG_SZ");
			output = output.substring(index + 6);
			output = output.substring(0, output.length() - 8);
			output = output.trim();
			output = StringUtil.stripQuotes(output);
			runner.runInBackground(output, url.toString());
		}
		else
		{
			runner.runInBackground("xdg-open", url.toString());
		}
	}

	/**
	 * Opens an URL with the default settings (which will typically open in an internal browser with no toolbar/url
	 * bar/etc).
	 * 
	 * @param url
	 * @return
	 */
	public static IWebBrowser openURL(String url)
	{
		try
		{
			IWorkbenchBrowserSupport workbenchBrowserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser webBrowser = workbenchBrowserSupport.createBrowser(null);
			if (webBrowser != null)
			{
				webBrowser.openURL(new URL(url));
			}
			return webBrowser;
		}
		catch (Exception e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
		return null;
	}
}
