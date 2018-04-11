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

import com.aptana.core.logging.IdeLog;
import com.aptana.ui.UIPlugin;

/**
 * @author ashebanow
 */
public class ViewReleaseNotesCommandHandler extends AbstractHandler
{

	private static final String RELEASE_NOTES_URL_STRING = "http://aptana.com/products/studio3/releasenotes"; //$NON-NLS-1$
	private static URL RELEASE_NOTES_URL;

	static
	{
		try
		{
			RELEASE_NOTES_URL = new URL(RELEASE_NOTES_URL_STRING);
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}
	}

	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		if (RELEASE_NOTES_URL == null)
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
								| IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.STATUS,
						"ViewReleaseNotes", //$NON-NLS-1$
						null, // Set the name to null. That way the browser tab will display the title of page loaded in
								// the browser.
						null).openURL(RELEASE_NOTES_URL);
			}
			else
			{
				support.getExternalBrowser().openURL(RELEASE_NOTES_URL);
			}
		}
		catch (PartInitException e)
		{
			IdeLog.logError(UIPlugin.getDefault(), e);
		}

		return null;
	}
}
