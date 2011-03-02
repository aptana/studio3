/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.util.EclipseUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.browser.PortalBrowserEditor;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.usage.PingStartup;

/**
 * The portal class is a singleton that controls the portal browser and allows interacting with it.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
@SuppressWarnings("restriction")
public class Portal
{
	public static final String BASE_URL_PREFIX = "http://toolbox.aptana.com/toolbox"; //$NON-NLS-1$

	// Note: 173.45.232.197 is the staging site
	// For debugging, do NOT check in with these uncommented:
	// public static final String BASE_URL_PREFIX = Platform.inDevelopmentMode() ? System.getProperty(
	//			"toolboxURL", "http://localhost:3000/toolbox") : "http://173.45.232.197/toolbox"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	private static final String BASE_REMOTE_URL = BASE_URL_PREFIX;
	private static final String BASE_LOCAL_URL = "/content/index.html"; //$NON-NLS-1$

	public static final String ACTIVE_PROJECT_KEY = "activeProject"; //$NON-NLS-1$
	private static final String RAILS_NATURE = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$
	private static final String PHP_NATURE = "com.aptana.editor.php.phpnature"; //$NON-NLS-1$
	private static final String WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$
	private static final String PYDEV_NATURE = "org.python.pydev.pythonNature"; //$NON-NLS-1$

	private static Portal instance;
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
	 * Returns true if the open-portal flag was set in the preferences.
	 * 
	 * @return True, if we should display the portal as part of the startup; False, otherwise (the user disabled it)
	 */
	public boolean shouldOpenPortal()
	{
		IPreferenceStore preferenceStore = PortalUIPlugin.getDefault().getPreferenceStore();
		return preferenceStore.getBoolean(IPortalPreferences.SHOULD_OPEN_DEV_TOOLBOX);
	}

	/**
	 * Opens the portal with a given URL. In case the portal is already open, and the given URL is valid, direct the
	 * portal to the new URL.<br>
	 * This method must be called from the UI thread (preferably, through a UIJob).
	 * 
	 * @param url
	 *            A URL (can be null).
	 * @param browserEditorId
	 *            the identifier of the browser-editor that was registered through the org.eclipse.ui.editors extension
	 *            point.
	 */
	public void openPortal(URL url, final String browserEditorId)
	{
		try
		{
			if (url == null)
			{
				url = getDefaultURL();
			}
			URL urlWithGetParams = new URL(url.toString() + getURLForProject(PortalUIPlugin.getActiveProject()));
			url = urlWithGetParams;
		}
		catch (IOException e)
		{
			PortalUIPlugin.logError(e);
			return;
		}
		if (portalBrowser != null && !portalBrowser.isDisposed())
		{
			// Refresh the URL, bring to front, and return
			IEditorSite editorSite = portalBrowser.getEditorSite();
			IWorkbenchPart part = editorSite.getPart();
			editorSite.getPage().activate(part);
			portalBrowser.setURL(url);
			return;
		}
		final URL finalURL = url;
		Job job = new UIJob("Launching Aptana Portal...") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				WebBrowserEditorInput input = new WebBrowserEditorInput(finalURL, 0, PortalUIPlugin.PORTAL_ID);
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try
				{
					portalBrowser = (PortalBrowserEditor) page.openEditor(input, browserEditorId);
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
	 * Returns the default URL for the portal.<br>
	 * In case we have a live Internet connection, return the remote content. Otherwise, return the local content.
	 * 
	 * @return A default URL (can be null)
	 * @throws IOException
	 */
	protected URL getDefaultURL() throws IOException
	{
		// Do a connection check
		if (isConnected())
		{
			return new URL(BASE_REMOTE_URL);
		}
		return FileLocator.toFileURL(Portal.class.getResource(BASE_LOCAL_URL));
	}

	/**
	 * Check for connection with the remote portal server.
	 * 
	 * @return True, if and only if the remote server is alive.
	 */
	private boolean isConnected()
	{
		boolean connected = false;
		HttpURLConnection connection = null;
		try
		{
			connection = (HttpURLConnection) new URL(BASE_REMOTE_URL).openConnection();
			// Give it a 4 seconds delay before deciding that it's a dead connection
			connection.setConnectTimeout(4000);
			connection.setRequestMethod("HEAD"); // Don't ask for content //$NON-NLS-1$
			connection.setAllowUserInteraction(false);
			connection.connect();
			connected = true;

		}
		catch (Exception e)
		{
			connected = false;
			PortalUIPlugin
					.logWarning("Could not establish a connection to the remote Aptana Dev Toolbox portal. Using the local portal content."); //$NON-NLS-1$
		}
		finally
		{
			if (connection != null)
				connection.disconnect();
		}
		return connected;
	}

	/**
	 * Build the URL GET parameters that will be appended to the original portal path.
	 * 
	 * @param activeProject
	 * @return The GET parameters string
	 */
	@SuppressWarnings("nls")
	protected String getURLForProject(final IProject activeProject)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("?v=");
		builder.append(getVersion());

		builder.append("&bg=");
		builder.append(toHex(getThemeManager().getCurrentTheme().getBackground()));
		builder.append("&fg=");
		builder.append(toHex(getThemeManager().getCurrentTheme().getForeground()));

		// "chrome"
		builder.append("&ch=");// FIXME Grab one of the actual parent widgets and grab it's bg?
		Color color = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		builder.append(toHex(color.getRGB()));

		// project type
		builder.append("&p=");
		builder.append(getProjectType(activeProject));

		// version control
		// builder.append("&vc=");
		// builder.append(getVersionControl());

		// github
		// builder.append("&gh=");
		// builder.append(hasGithubRemote() ? '1' : '0');

		// timestamp to force updates to server (bypass browser cache)
		builder.append("&ts=");
		builder.append(System.currentTimeMillis());

		// guid that relates to a single install of the IDE
		builder.append("&id=");
		builder.append(getGUID());

		// deploy info
		builder.append(getDeployParam(activeProject));

		// for debugging output
		// builder.append("&debug=1");
		return builder.toString();
	}

	@SuppressWarnings("nls")
	protected String getDeployParam(IProject selectedProject)
	{
		if (selectedProject != null && selectedProject.exists())
		{
			IFile file = selectedProject.getFile("deploy/default.rb");
			if (file.exists())
				return "&dep=ch";
			file = selectedProject.getFile("deploy/solo.rb");
			if (file.exists())
				return "&dep=cs";
			file = selectedProject.getFile("Capfile");
			if (file.exists())
				return "&dep=cap";
			file = selectedProject.getFile("capfile");
			if (file.exists())
				return "&dep=cap";
		}
		return "";
	}

	/**
	 * Returns the portal plugin version
	 * 
	 * @return
	 */
	protected String getVersion()
	{
		return EclipseUtil.getPluginVersion(PortalUIPlugin.getDefault());
	}

	/**
	 * Get the theme manager.
	 * 
	 * @return
	 */
	protected IThemeManager getThemeManager()
	{
		return ThemePlugin.getDefault().getThemeManager();
	}

	protected String getGUID()
	{
		return PingStartup.getApplicationId();
	}

	protected char getProjectType(IProject selectedProject)
	{
		if (selectedProject != null)
		{
			// R for Rails, D for pydev, W for web, P for PHP and O for other.
			try
			{
				if (selectedProject.hasNature(WEB_NATURE))
				{
					return 'W';
				}
				else if (selectedProject.hasNature(RAILS_NATURE))
				{
					return 'R';
				}
				else if (selectedProject.hasNature(PHP_NATURE))
				{
					return 'P';
				}
				else if (selectedProject.hasNature(PYDEV_NATURE))
				{
					return 'D';
				}
			}
			catch (CoreException e)
			{
				PortalUIPlugin.logError(e);
			}
		}
		return 'O';
	}

	private String toHex(RGB rgb)
	{
		// FIXME This and pad are copy-pasted from Theme class
		return MessageFormat.format("{0}{1}{2}", pad(Integer.toHexString(rgb.red), 2, '0'), pad(Integer //$NON-NLS-1$
				.toHexString(rgb.green), 2, '0'), pad(Integer.toHexString(rgb.blue), 2, '0'));
	}

	private String pad(String string, int desiredLength, char padChar)
	{
		while (string.length() < desiredLength)
		{
			string = padChar + string;
		}
		return string;
	}

	/**
	 * Listen to the portal disposal and do some cleanup.
	 */
	private class PortalDisposeListener implements DisposeListener
	{
		public void widgetDisposed(DisposeEvent e)
		{
			portalBrowser = null;
		}

	}
}
