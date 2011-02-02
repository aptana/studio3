/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.ui.internal.commands;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.ui.UIPlugin;

public class ReportBugCommandHandler extends AbstractHandler
{

	private static final String REPORT_BUG_URL_STRING = "https://aptana.lighthouseapp.com/projects/35272-studio/tickets/new"; //$NON-NLS-1$
	private static URL REPORT_BUG_URL;

	static
	{
		try
		{
			REPORT_BUG_URL = new URL(REPORT_BUG_URL_STRING);
		}
		catch (MalformedURLException e)
		{
			UIPlugin.log(e);
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (REPORT_BUG_URL == null)
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
								| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS, "ReportBug", //$NON-NLS-1$
						null, // Set the name to null. That way the browser tab will display the title of page loaded in the browser.
						null).openURL(REPORT_BUG_URL);
			}
			else
			{
				support.getExternalBrowser().openURL(REPORT_BUG_URL);
			}
		}
		catch (PartInitException e)
		{
			UIPlugin.log(e);
		}

		return null;
	}
}
