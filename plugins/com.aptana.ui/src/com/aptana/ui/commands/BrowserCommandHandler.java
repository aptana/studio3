/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.core.logging.IdeLog;
import com.aptana.ui.UIPlugin;

class BrowserCommandHandler extends AbstractHandler
{

	private URL browserURL;
	private String browserId;

	BrowserCommandHandler(String url, String browserId)
	{
		setURL(url);
		this.browserId = browserId;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (browserURL == null)
		{
			return null;
		}

		try
		{
			IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

			if (support.isInternalWebBrowserAvailable())
			{
				support.createBrowser(
						IWorkbenchBrowserSupport.NAVIGATION_BAR | IWorkbenchBrowserSupport.LOCATION_BAR
								| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS, browserId,
						null, // Set the name to null so that the browser tab will display the title of page loaded in
								// the browser
						null).openURL(browserURL);
			}
			else
			{
				support.getExternalBrowser().openURL(browserURL);
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}

		return null;
	}

	/**
	 * Sets the url that the command should open.
	 * 
	 * @param url
	 *            the url string
	 */
	protected void setURL(String url)
	{
		try
		{
			browserURL = new URL(url);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
	}
}
