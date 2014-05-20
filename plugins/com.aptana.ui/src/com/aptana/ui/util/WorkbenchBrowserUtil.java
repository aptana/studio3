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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.program.Program;
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
public class WorkbenchBrowserUtil
{

	private static final Pattern ARG_SPLITTER = Pattern.compile("([^\"]\\S*|\".+?\")\\s*"); //$NON-NLS-1$

	private IProcessRunner runner;
	private IWorkbenchBrowserSupport support;

	/**
	 * 
	 */
	private WorkbenchBrowserUtil()
	{
		this(new ProcessRunner(), PlatformUI.getWorkbench().getBrowserSupport());
	}

	protected WorkbenchBrowserUtil(IProcessRunner runner, IWorkbenchBrowserSupport support)
	{
		this.runner = runner;
		this.support = support;
	}

	public static void launchExternalBrowser(String url)
	{
		try
		{
			launchExternalBrowser(new URL(url));
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
	}

	public static void launchExternalBrowser(URL url)
	{
		launchExternalBrowser(url, null);
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

	public static IWebBrowser launchExternalBrowser(URL url, String browserId)
	{
		return new WorkbenchBrowserUtil().doLaunchExternalBrowser(url, browserId);
	}

	IWebBrowser doLaunchExternalBrowser(URL url, String browserId)
	{
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
	public static void launchBrowserByCommand(URL url)
	{
		new WorkbenchBrowserUtil().doLaunchBrowserByCommand(url);
	}

	@SuppressWarnings("nls")
	void doLaunchBrowserByCommand(URL url)
	{
		if (launchProgram(url))
		{
			return;
		}

		// Can we fall back to running a command to load the URL?
		if (isMac())
		{
			runner.runInBackground("open", url.toString());
		}
		else if (isWindows())
		{
			List<String> args = new ArrayList<String>();
			IStatus result = runner.runInBackground("reg", "query", "HKEY_CLASSES_ROOT\\http\\shell\\open\\command");
			if (result.isOK())
			{
				String output = result.getMessage();
				int index = output.indexOf("REG_SZ");
				if (index != -1)
				{
					output = output.substring(index + 6);
					// Split by lines, take first line, remove leading and trailing whitespace
					String firstLine = StringUtil.LINE_SPLITTER.split(output)[0].trim();
					// Now grab all the args which are delimited by spaces, unless quoted.
					Matcher m = ARG_SPLITTER.matcher(firstLine);
					while (m.find())
					{
						// Replace %1 with the url we want to hit
						args.add(StringUtil.stripQuotes(m.group(1)).replace("%1", url.toString()));
					}
				}
			}
			// We failed to grab the location of the default browser from the registry!
			if (args.isEmpty())
			{
				args.add("iexplore.exe");
				args.add(url.toString());
			}
			runner.runInBackground(args.toArray(new String[args.size()]));
		}
		else
		{
			runner.runInBackground("xdg-open", url.toString());
		}
	}

	/**
	 * Attempts to use {@link Program#launch(String)} to open a URL.
	 * 
	 * @param url
	 * @return
	 */
	protected boolean launchProgram(URL url)
	{
		return Program.launch(url.toString());
	}

	protected boolean isWindows()
	{
		return PlatformUtil.isWindows();
	}

	protected boolean isMac()
	{
		return PlatformUtil.isMac();
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
		return new WorkbenchBrowserUtil().doOpenURL(url);
	}

	IWebBrowser doOpenURL(String url)
	{
		try
		{
			IWebBrowser webBrowser = support.createBrowser(null);
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
