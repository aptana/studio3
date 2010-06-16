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

	private static final String REPORT_BUG_URL_STRING = "https://aptana.lighthouseapp.com/projects/45260-rr3/tickets/new"; //$NON-NLS-1$
	private static URL REPORT_BUG_URL;

	static
	{
		try
		{
			REPORT_BUG_URL = new URL(REPORT_BUG_URL_STRING);
		}
		catch (MalformedURLException e)
		{
			UIPlugin.logError(e.getLocalizedMessage(), e);
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
			UIPlugin.logError(e.getLocalizedMessage(), e);
		}

		return null;
	}
}
