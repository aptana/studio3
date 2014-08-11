/**
 * Aptana Studio
 * Copyright (c) 2005-2011 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.internal.startpage;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;

import com.aptana.core.CorePlugin;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.EclipseUtil;
import com.aptana.portal.ui.IDebugScopes;
import com.aptana.portal.ui.IPortalPreferences;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.internal.Portal;

/**
 * Aptana Studio Start Page utilities class.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class StartPageUtil
{
	/**
	 * Returns the string to the Start-Page URL
	 * 
	 * @return the Aptana Studio start page URL
	 */
	public static String getStartPageURL()
	{
		String dashboardUrl = EclipseUtil.getSystemProperty(IStartPageUISystemProperties.START_PAGE_URL);
		if (dashboardUrl == null)
		{
			dashboardUrl = StartPageBrowserEditor.STUDIO_START_PAGE_URL;
		}
		return dashboardUrl;
	}

	/**
	 * Show the Start-Page.
	 * 
	 * @param bringToTop
	 */
	public static void showStartPage(boolean bringToTop)
	{
		showStartPage(bringToTop, null);
	}

	/**
	 * Show the Start-Page.
	 * 
	 * @param bringToTop
	 * @param additionalParameters
	 *            - Additional GET parameters that will be added to the URL (may be <code>null</code>)
	 */
	public static void showStartPage(boolean bringToTop, Map<String, String> additionalParameters)
	{
		try
		{
			Portal.getInstance().openPortal(new URL(getStartPageURL()), StartPageBrowserEditor.WEB_BROWSER_EDITOR_ID,
					bringToTop, additionalParameters);
			// Update the preference key with the current studio's version
			String currentVersion = getCurrentVersion();
			if (currentVersion != null)
			{
				// cache that version for the next startup
				IEclipsePreferences store = InstanceScope.INSTANCE.getNode(PortalUIPlugin.PLUGIN_ID);
				store.put(IPortalPreferences.LAST_KNOWN_STUDIO_VERSION, currentVersion);
				try
				{
					store.flush();
				}
				catch (BackingStoreException e)
				{
					IdeLog.logWarning(PortalUIPlugin.getDefault(),
							"Error saving the last studio version", e, IDebugScopes.START_PAGE); //$NON-NLS-1$
				}
			}
		}
		catch (MalformedURLException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), "Malformed URL for the Studio's Start Page", e); //$NON-NLS-1$
		}
	}

	/**
	 * Returns true in case the start page should be displayed.<br>
	 * We only display the start page once, after a Studio update/install. This method looks for the preference value of
	 * the last version that existed when the page was displayed, then, checks the current version to see if we need to
	 * show the page again.
	 * 
	 * @return True, if we need to show the start page; False, otherwise.
	 */
	public static boolean shouldShowStartPage()
	{
		if (EclipseUtil.getPluginVersion("com.appcelerator.titanium.rcp") != null) //$NON-NLS-1$
		{
			return false;
		}
		IEclipsePreferences store = InstanceScope.INSTANCE.getNode(PortalUIPlugin.PLUGIN_ID);
		String lastVersion = store.get(IPortalPreferences.LAST_KNOWN_STUDIO_VERSION, null);
		if (lastVersion == null)
		{
			return true;
		}
		String currentVersion = getCurrentVersion();
		if (currentVersion == null)
		{
			// We don't want to open the start page in case we have an error determining the studio's version.
			return false;
		}
		// Show the start page if the versions are different.
		return !lastVersion.equals(currentVersion);
	}

	/**
	 * Returns the Studio's current version.<br>
	 * The check is done by looking at the Studio's feature version.
	 * 
	 * @return A String version; Null, in case there was an error.
	 */
	private static String getCurrentVersion()
	{
		String version = CorePlugin.getAptanaStudioVersion();
		if (version == null && !Platform.inDevelopmentMode())
		{
			// we have a problem...
			IdeLog.logError(PortalUIPlugin.getDefault(), "Could not identify the Studio's version"); //$NON-NLS-1$
		}
		return version;
	}
}
