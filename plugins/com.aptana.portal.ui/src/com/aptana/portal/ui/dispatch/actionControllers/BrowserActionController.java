/**
 * Aptana Studio
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the GNU Public License (GPL) v3 (with exceptions).
 * Please see the license.html included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.core.logging.IdeLog;
import com.aptana.core.util.IBrowserUtil.BrowserInfo;
import com.aptana.jetty.util.epl.ajax.JSON;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.browser.PortalBrowserEditor;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.internal.Portal;
import com.aptana.ui.BrowserManager;
import com.aptana.ui.util.WorkbenchBrowserUtil;

/**
 * An action controller for browser-related actions.
 * 
 * @author Shalom Gibly <sgibly@appcelerator.com>
 */
public class BrowserActionController extends AbstractActionController
{
	// ////////////////////////// Actions /////////////////////////////

	/**
	 * Refresh the portal by trying to re-connect to the remote content.<br>
	 * This action is useful when the user was offline while the portal was opened. In that case, a 'reload' button on
	 * the offline content will call this function in order to re-initiate the portal loading.<br>
	 * Note: The refresh is done on the PortalBrowserEditor.WEB_BROWSER_EDITOR_ID portal only (at this time)
	 */
	@ControllerAction
	public Object refreshPortal(Object attributes)
	{
		URL url = null;
		if (attributes instanceof Object[] && ((Object[]) attributes).length > 0)
		{
			url = getURL(attributes);
		}
		Portal.getInstance().openPortal(url, PortalBrowserEditor.WEB_BROWSER_EDITOR_ID, false, null);
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Opens an internal/external browser with the URL that is given in the attributes. The browser selection follows
	 * the Studio's browsers setting.
	 * 
	 * @param attributes
	 *            We expect for an array that contains a single string URL.
	 * @return {@link IBrowserNotificationConstants#JSON_OK} or a {@link IBrowserNotificationConstants#JSON_ERROR}
	 * @since Aptana Studio 3.3.0
	 */
	@ControllerAction
	public Object openURL(Object attributes)
	{
		URL url = getURL(attributes);
		if (url == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		boolean ok = WorkbenchBrowserUtil.openURL(url.toString()) != null;
		if (!ok)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Opens an internal browser with the URL that is given in the attributes.
	 * 
	 * @param attributes
	 *            We expect for an array that contains a single string URL.
	 * @return {@link IBrowserNotificationConstants#JSON_OK} or a {@link IBrowserNotificationConstants#JSON_ERROR}
	 */
	@ControllerAction
	public Object internalOpen(Object attributes)
	{
		URL url = getURL(attributes);
		if (url == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		try
		{
			IWebBrowser browser = PortalUIPlugin
					.getDefault()
					.getWorkbench()
					.getBrowserSupport()
					.createBrowser(
							IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR
									| IWorkbenchBrowserSupport.STATUS | IWorkbenchBrowserSupport.NAVIGATION_BAR,
							url.toString(), null, null);
			browser.openURL(url);
		}
		catch (PartInitException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Opens an external browser with the URL that is given in the attributes.
	 * 
	 * @param attributes
	 *            We expect for an array that contains a single string URL.
	 * @return {@link IBrowserNotificationConstants#JSON_OK} or a {@link IBrowserNotificationConstants#JSON_ERROR}
	 */
	@ControllerAction
	public Object externalOpen(Object attributes)
	{
		URL url = getURL(attributes);
		if (url == null)
		{
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		try
		{
			IWebBrowser browser = PortalUIPlugin
					.getDefault()
					.getWorkbench()
					.getBrowserSupport()
					.createBrowser(IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.STATUS,
							url.toString(), null, null);
			browser.openURL(url);
		}
		catch (PartInitException e)
		{
			IdeLog.logError(PortalUIPlugin.getDefault(), e);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
	}

	/**
	 * Returns the currently detected Eclipse configured browsers.
	 * 
	 * <pre>
	 *  JavaScript usage:
	 *  
	 *  // browsers will hold a location to name map
	 *  var browsers = dispatch(
	 *    $H({
	 *      controller : 'portal.browser',
	 *      action     : "getConfiguredBrowsers"
	 *    }).toJSON()).evalJSON();
	 * </pre>
	 * 
	 * @return A JSON map that contains all the existing browsers detected in Eclipse (location to name mapping).
	 */
	@ControllerAction
	public Object getConfiguredBrowsers()
	{
		Map<String, String> browsers = new HashMap<String, String>(6);
		List<BrowserInfo> webBrowsers = BrowserManager.getInstance().getWebBrowsers();
		for (BrowserInfo descriptor : webBrowsers)
		{
			browsers.put(resolveBrowserLocation(descriptor.getLocation()), descriptor.getName());
		}

		return JSON.toString(browsers);
	}

	/**
	 * Detect and configure the system's installed browsers as Eclipse browser references.
	 * 
	 * <pre>
	 *  JavaScript usage:
	 *  
	 *  // newBrowsers will hold a location to name map
	 *  var newBrowsers = dispatch(
	 *    $H({
	 *      controller : 'portal.browser',
	 *      action     : "configureBrowsers"
	 *    }).toJSON()).evalJSON();
	 * </pre>
	 * 
	 * @return A JSON map that contains all the added browsers (location to name mapping), or an empty map in case no
	 *         new browser was added.
	 */
	@ControllerAction
	public Object configureBrowsers()
	{
		Map<String, String> result = new HashMap<String, String>(6);
		Collection<BrowserInfo> browsersFound = BrowserManager.getInstance().searchMoreBrowsers();
		for (BrowserInfo browserInfo : browsersFound)
		{
			result.put(resolveBrowserLocation(browserInfo.getLocation()), browserInfo.getName());
		}
		return JSON.toString(result);
	}

	/**
	 * Resolve the full path to the browser location.
	 * 
	 * @param location
	 * @return The browser location; <code>null</code> if it's the default eclipse browser.
	 */
	private static String resolveBrowserLocation(String location)
	{
		if (location == null)
		{
			// Default eclipse browser
			return "null"; //$NON-NLS-1$
		}
		File file = new File(location);
		String path;
		try
		{
			path = file.getCanonicalPath();
		}
		catch (IOException e)
		{
			path = file.getAbsolutePath();
		}
		return path;
	}

	/**
	 * Returns the URL from the attributes. Null, if an error occured.
	 * 
	 * @param attributes
	 * @return A URL, or null if an error occurs.
	 */
	private URL getURL(Object attributes)
	{
		if (attributes instanceof Object[])
		{
			Object[] arr = (Object[]) attributes;
			if (arr.length == 1)
			{
				try
				{
					return new URL(arr[0].toString());
				}
				catch (MalformedURLException e)
				{
					IdeLog.logError(PortalUIPlugin.getDefault(), "Invalid URL: " + arr[0], e); //$NON-NLS-1$
				}
			}
			else
			{
				String message = "Wrong argument count passed to BrowserActionController::getURL. Expected 1 and got " + arr.length; //$NON-NLS-1$
				IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
			}
		}
		else
		{
			String message = "Wrong argument type passed to BrowserActionController::getURL. Expected Object[] and got " //$NON-NLS-1$
					+ ((attributes == null) ? "null" : attributes.getClass().getName()); //$NON-NLS-1$
			IdeLog.logError(PortalUIPlugin.getDefault(), new Exception(message));
		}
		return null;
	}

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Do nothing
	}
}
