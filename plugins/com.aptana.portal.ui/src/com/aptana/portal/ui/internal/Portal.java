/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.WebBrowserEditorInput;
import org.eclipse.ui.internal.registry.EditorRegistry;
import org.eclipse.ui.progress.UIJob;

import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.core.util.StringUtil;
import com.aptana.core.util.URLUtil;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.browser.AbstractPortalBrowserEditor;
import com.aptana.theme.IThemeManager;
import com.aptana.theme.ThemePlugin;
import com.aptana.ui.util.UIUtils;
import com.aptana.usage.UsagePlugin;

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
	protected static final String BASE_REMOTE_URL = BASE_URL_PREFIX;
	protected static final String BASE_LOCAL_URL = "/content/index.html"; //$NON-NLS-1$

	public static final String ACTIVE_PROJECT_KEY = "activeProject"; //$NON-NLS-1$
	protected static final String RAILS_NATURE = "org.radrails.rails.core.railsnature"; //$NON-NLS-1$
	protected static final String PHP_NATURE = "com.aptana.editor.php.phpnature"; //$NON-NLS-1$
	protected static final String WEB_NATURE = "com.aptana.projects.webnature"; //$NON-NLS-1$
	protected static final String PYDEV_NATURE = "org.python.pydev.pythonNature"; //$NON-NLS-1$

	private Map<IWorkbenchWindow, AbstractPortalBrowserEditor> portalBrowsers;
	private static Portal instance;

	// Private constructor
	private Portal()
	{
		portalBrowsers = new HashMap<IWorkbenchWindow, AbstractPortalBrowserEditor>();
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
	 * This method must be called from the UI thread (preferably, through a UIJob).<br>
	 * By default, this method will open the portal as the top editor.
	 * 
	 * @param url
	 *            A URL (can be null).
	 * @param browserEditorId
	 *            the identifier of the browser-editor that was registered through the org.eclipse.ui.editors extension
	 *            point.
	 * @see #openPortal(URL, String, boolean)
	 */
	public void openPortal(URL url, final String browserEditorId)
	{
		openPortal(url, browserEditorId, true, null);
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
	 * @param bringToTop
	 *            Indicate whether the opened portal should be brought to the top when opened.
	 * @param additionalParameters
	 *            An optional map that may hold additional GET parameters that will be appended to the opened URL.
	 */
	public void openPortal(URL url, final String browserEditorId, final boolean bringToTop,
			Map<String, String> additionalParameters)
	{
		try
		{
			if (url == null)
			{
				url = getDefaultURL();
			}
			else if (!isConnected(url))
			{
				URL localURL = FileLocator.toFileURL(Portal.class.getResource(BASE_LOCAL_URL));
				url = URLUtil.appendParameters(localURL, new String[] { "url", url.toString() }); //$NON-NLS-1$
			}
			Map<String, String> parameters = getURLParametersForProject(PortalUIPlugin.getActiveProject());
			if (additionalParameters != null)
			{
				parameters.putAll(additionalParameters);
			}
			url = URLUtil.appendParameters(url, parameters, false);
		}
		catch (IOException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return;
		}

		final URL finalURL = url;
		Job job = new UIJob("Launching the Studio portal...") //$NON-NLS-1$
		{
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				final IWorkbenchWindow workbenchWindow = UIUtils.getActiveWorkbenchWindow();

				if (workbenchWindow == null)
				{
					return Status.OK_STATUS;
				}

				// TISTUD-5510 If there are existing busted portal/dashboard tabs, remove them
				IWorkbenchPage activePage = workbenchWindow.getActivePage();
				IEditorReference[] refs = activePage.getEditorReferences();
				for (IEditorReference ref : refs)
				{
					String editorId = ref.getId();
					if (EditorRegistry.EMPTY_EDITOR_ID.equals(editorId))
					{
						activePage.closeEditors(new IEditorReference[] { ref }, false);
					}
				}

				AbstractPortalBrowserEditor portalBrowser = portalBrowsers.get(workbenchWindow);
				if (portalBrowser != null && !portalBrowser.isDisposed())
				{
					// Refresh the URL, bring to front, and return
					IEditorSite editorSite = portalBrowser.getEditorSite();
					IWorkbenchPart part = editorSite.getPart();
					editorSite.getPage().activate(part);
					portalBrowser.setURL(finalURL);
					return Status.OK_STATUS;
				}

				WebBrowserEditorInput input = new WebBrowserEditorInput(finalURL, 0, PortalUIPlugin.PORTAL_ID);
				IWorkbenchPage page = UIUtils.getActivePage();
				if (page == null)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(),
							"Cannot open the Studio portal page. No active workbench page is found."); //$NON-NLS-1$
					return Status.CANCEL_STATUS;
				}
				if (!bringToTop)
				{
					// In case the portal should not be opened as the top-editor, make sure we open it in a way it stays
					// in the back.
					IEditorPart activeEditor = page.getActiveEditor();
					if (activeEditor != null)
					{
						try
						{
							// We use openEditors() to manipulate the opening order. Otherwise, using the regular
							// openEditor() will bring the editor to the top no matter what.
							IEditorReference[] editors = page.openEditors(
									new IEditorInput[] { activeEditor.getEditorInput(), input }, new String[] {
											activeEditor.getEditorSite().getId(), browserEditorId },
									IWorkbenchPage.MATCH_INPUT);
							portalBrowser = (AbstractPortalBrowserEditor) editors[1].getEditor(true);
						}
						catch (Exception e)
						{
							// catch any exception here
							IdeLog.logError(PortalUIPlugin.getDefault(),
									"Could not open the Studio portal as a 'non-focused' editor", e); //$NON-NLS-1$
						}
					}
				}
				try
				{
					if (portalBrowser == null)
					{
						// Just open the portal using the openEditor. The editor will be brought to the top.
						portalBrowser = (AbstractPortalBrowserEditor) page.openEditor(input, browserEditorId);
					}
					if (portalBrowser != null)
					{
						portalBrowser.addDisposeListener(new DisposeListener()
						{
							public void widgetDisposed(DisposeEvent e)
							{
								portalBrowsers.remove(workbenchWindow);
							}
						});
					}
				}
				catch (PartInitException e)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), "Failed to open the Studio portal", e); //$NON-NLS-1$
				}
				if (portalBrowser != null)
				{
					portalBrowsers.put(workbenchWindow, portalBrowser);
				}
				return Status.OK_STATUS;
			}
		};
		EclipseUtil.setSystemForJob(job);
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
		return getDefaultURL(new URL(BASE_REMOTE_URL), Portal.class.getResource(BASE_LOCAL_URL));
	}

	/**
	 * Returns the default URL for the portal.<br>
	 * In case we have a live Internet connection, return the remote content. Otherwise, return the local content.
	 * 
	 * @return A default URL (can be null)
	 * @throws IOException
	 */
	protected URL getDefaultURL(URL desiredUrl, URL fallbackUrl) throws IOException
	{
		if (fallbackUrl == null)
		{
			throw new IllegalArgumentException("Fallback URL must not be null"); //$NON-NLS-1$
		}

		// Do a connection check
		if (isConnected(desiredUrl))
		{
			return desiredUrl;
		}
		return FileLocator.toFileURL(fallbackUrl);
	}

	/**
	 * Check for connection with the remote portal server.
	 * 
	 * @return True, if and only if the remote server is alive.
	 */
	private boolean isConnected(URL url)
	{
		boolean connected = false;
		HttpURLConnection connection = null;
		try
		{
			// If the URL is for a local file, return 'true'
			if ("file".equalsIgnoreCase(url.getProtocol())) //$NON-NLS-1$
			{
				return true;
			}
			connection = (HttpURLConnection) url.openConnection();
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
			IdeLog.logWarning(PortalUIPlugin.getDefault(),
					"Could not establish a connection to the remote portal. Using the local content."); //$NON-NLS-1$
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
	protected Map<String, String> getURLParametersForProject(final IProject activeProject)
	{
		final Map<String, String> builder = new HashMap<String, String>();
		builder.putAll(URLUtil.getDefaultParameters());

		builder.put("bg", toHex(getThemeManager().getCurrentTheme().getBackground()));
		builder.put("fg", toHex(getThemeManager().getCurrentTheme().getForeground()));

		// "chrome"
		Color color = PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		builder.put("ch", toHex(color.getRGB()));// FIXME Grab one of the actual parent widgets and grab it's bg?

		// project type
		builder.put("p", String.valueOf(getProjectType(activeProject)));

		// version control
		// builder.append("&vc=");
		// builder.append(getVersionControl());

		// github
		// builder.append("&gh=");
		// builder.append(hasGithubRemote() ? '1' : '0');

		// timestamp to force updates to server (bypass browser cache)
		builder.put("ts", String.valueOf(System.currentTimeMillis()));

		// guid that relates to a single install of the IDE
		builder.put("id", getGUID());

		// deploy info
		builder.putAll(getDeployParam(activeProject));

		// for debugging output
		// builder.append("&debug=1");
		return builder;
	}

	@SuppressWarnings("nls")
	protected Map<String, String> getDeployParam(IProject selectedProject)
	{
		final Map<String, String> builder = new HashMap<String, String>();
		if (selectedProject != null && selectedProject.exists())
		{
			IFile file = selectedProject.getFile("deploy/default.rb");
			if (file.exists())
				builder.put("dep", "ch");
			file = selectedProject.getFile("deploy/solo.rb");
			if (file.exists())
				builder.put("dep", "cs");
			file = selectedProject.getFile("Capfile");
			if (file.exists())
				builder.put("dep", "cap");
			file = selectedProject.getFile("capfile");
			if (file.exists())
				builder.put("dep", "cap");
		}
		return builder;
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
		return UsagePlugin.getApplicationId();
	}

	protected char getProjectType(IProject selectedProject)
	{
		if (selectedProject != null && selectedProject.isAccessible())
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
				IdeLog.logError(PortalUIPlugin.getDefault(), e);
			}
		}
		return 'O';
	}

	private String toHex(RGB rgb)
	{
		// FIXME This and pad are copy-pasted from Theme class
		return MessageFormat.format(
				"{0}{1}{2}", StringUtil.pad(Integer.toHexString(rgb.red), 2, '0'), StringUtil.pad(Integer //$NON-NLS-1$
						.toHexString(rgb.green), 2, '0'), StringUtil.pad(Integer.toHexString(rgb.blue), 2, '0'));
	}
}
