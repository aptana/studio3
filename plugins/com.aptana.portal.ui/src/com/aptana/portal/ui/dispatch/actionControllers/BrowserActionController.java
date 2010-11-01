/**
 * This file Copyright (c) 2005-2010 Aptana, Inc. This program is
 * dual-licensed under both the Aptana Public License and the GNU General
 * Public license. You may elect to use one or the other of these licenses.
 * 
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by whichever of
 * the GPL or APL you select, is prohibited.
 *
 * 1. For the GPL license (GPL), you can redistribute and/or modify this
 * program under the terms of the GNU General Public License,
 * Version 3, as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other free and open source software ("FOSS") code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * 2. For the Aptana Public License (APL), this program and the
 * accompanying materials are made available under the terms of the APL
 * v1.0 which accompanies this distribution, and is available at
 * http://www.aptana.com/legal/apl/.
 * 
 * You may view the GPL, Aptana's exception and additional terms, and the
 * APL in the file titled license.html at the root of the corresponding
 * plugin containing this source file.
 * 
 * Any modifications to this file must keep this entire header intact.
 */
package com.aptana.portal.ui.dispatch.actionControllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.aptana.configurations.processor.ConfigurationStatus;
import com.aptana.portal.ui.PortalUIPlugin;
import com.aptana.portal.ui.dispatch.IBrowserNotificationConstants;
import com.aptana.portal.ui.internal.Portal;

/**
 * An action controller for opening a browser page internally or externally.
 * 
 * @author Shalom Gibly <sgibly@aptana.com>
 */
public class BrowserActionController extends AbstractActionController
{
	// ////////////////////////// Actions /////////////////////////////

	/**
	 * Refresh the portal by trying to re-connect to the remote content.<br>
	 * This action is useful when the user was offline while the portal was opened. In that case, a 'reload' button on
	 * the offline content will call this function in order to re-initiate the portal loading.
	 */
	@ControllerAction
	public Object refreshPortal(Object attributes)
	{
		Portal.getInstance().openPortal(null);
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
			IWebBrowser browser = PortalUIPlugin.getDefault().getWorkbench().getBrowserSupport().createBrowser(
					IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR
							| IWorkbenchBrowserSupport.STATUS | IWorkbenchBrowserSupport.NAVIGATION_BAR,
					url.toString(), null, null);
			browser.openURL(url);
		}
		catch (PartInitException e)
		{
			PortalUIPlugin.logError(e);
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
			IWebBrowser browser = PortalUIPlugin.getDefault().getWorkbench().getBrowserSupport().createBrowser(
					IWorkbenchBrowserSupport.AS_EXTERNAL | IWorkbenchBrowserSupport.STATUS, url.toString(), null, null);
			browser.openURL(url);
		}
		catch (PartInitException e)
		{
			PortalUIPlugin.logError(e);
			return IBrowserNotificationConstants.JSON_ERROR;
		}
		return IBrowserNotificationConstants.JSON_OK;
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
					PortalUIPlugin.logError("Invalid URL: " + arr[0], e); //$NON-NLS-1$
				}
			}
			else
			{
				PortalUIPlugin
						.logError(new Exception(
								"Wrong argument count passed to BrowserActionController::getURL. Expected 1 and got " + arr.length));//$NON-NLS-1$
			}
		}
		else
		{
			PortalUIPlugin.logError(new Exception(
					"Wrong argument type passed to BrowserActionController::getURL. Expected Object[] and got " //$NON-NLS-1$
							+ ((attributes == null) ? "null" : attributes.getClass().getName()))); //$NON-NLS-1$s
		}
		return null;
	}

	public void configurationStateChanged(ConfigurationStatus status, Set<String> attributesChanged)
	{
		// Do nothing
	}
}
