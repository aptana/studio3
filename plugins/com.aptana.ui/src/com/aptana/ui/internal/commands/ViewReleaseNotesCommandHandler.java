/**
 * 
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
			UIPlugin.logError(e.getLocalizedMessage(), e);
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
						null, // Set the name to null. That way the browser tab will display the title of page loaded in the browser.
						null).openURL(RELEASE_NOTES_URL);
			}
			else
			{
				support.getExternalBrowser().openURL(RELEASE_NOTES_URL);
			}
		}
		catch (PartInitException e)
		{
			UIPlugin.logError(e.getLocalizedMessage(), e);
		}

		return null;
	}
}
