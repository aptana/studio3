package com.aptana.portal.ui.internal;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.progress.UIJob;

import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.browser.PortalBrowserEditor;

/**
 * The portal class is a singleton that controls the portal browser and allows interacting with it.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class Portal
{
	public static final String BASE_URL_PREFIX = "http://toolbox.aptana.com/"; //$NON-NLS-1$
	private static Portal instance;
	private static URL BASE_LOCAL_URL;
	{
		try
		{
			BASE_LOCAL_URL = FileLocator.toFileURL(Portal.class.getResource("/content/index.html")); //$NON-NLS-1$
		}
		catch (IOException e)
		{
		}
	}
	private PortalBrowserEditor portalBrowser;

	// Private constructor
	private Portal()
	{
	}

	/**
	 * Returns a Portal instance.
	 * 
	 * @return A singleton portal instance
	 */
	public static Portal getInstance()
	{
		if (instance == null)
		{
			instance = new Portal();
		}
		return instance;
	}

	/**
	 * Opens the portal with a given URL. In case the portal is already open, and the given URL is valid, direct the
	 * portal to the new URL.
	 * 
	 * @param url
	 *            A URL (can be null).
	 */
	public void openPortal(URL url)
	{
		if (url == null)
		{
			url = getDefaultURL();
		}
		if (portalBrowser != null && !portalBrowser.isDisposed())
		{
			portalBrowser.setURL(url);
		}
		final URL finalURL = url;
		// TODO: Shalom - Put a condition on this startup to not load the portal
		// when it was already loaded once and the user set up everything needed.
		Job job = new UIJob("Aptana Portal Launch Job") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				WebBrowserEditorInput input = new WebBrowserEditorInput(finalURL, 0, PortalUIPlugin.PORTAL_ID);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try
				{
					portalBrowser = (PortalBrowserEditor) page.openEditor(input,
							PortalBrowserEditor.WEB_BROWSER_EDITOR_ID);
					portalBrowser.addDisposeListener(new PortalDisposeListener());
				}
				catch (PartInitException e)
				{
					PortalUIPlugin.logError("Cannot open Aptana Portal", e); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Returns the default URL for the portal.
	 * 
	 * @return A default URL (can be null)
	 */
	protected URL getDefaultURL()
	{
		return BASE_LOCAL_URL;
		// TODO: Shalom - Do a test for network and decide what to return
		/*
		 * try { return new URL(BASE_URL_PREFIX); } catch (MalformedURLException e) { return null; }
		 */
	}

	/**
	 * Listen to the portal disposal and do some cleanup.
	 */
	private class PortalDisposeListener implements DisposeListener
	{
		@Override
		public void widgetDisposed(DisposeEvent e)
		{
			portalBrowser = null;
		}

	}
}
